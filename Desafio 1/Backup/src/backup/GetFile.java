package backup;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.util.Properties;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;

public class GetFile
extends Thread
{
	Properties props;
	int contadorArquivosEnviados;
	String dataConexao;
	String dataUltimoEnvio;
	FTPClient ftpClient;



	public GetFile(boolean flagRestart, Properties properties)
	{
		this.timeStampVerificacaoInterna = Long.valueOf(System.currentTimeMillis());
		setFlagRestart(flagRestart);
		this.props = properties;
	}

	public FTPClient getFtpClient()
	{
		return this.ftpClient;
	}

	public void setFtpClient(FTPClient ftpClient)
	{
		this.ftpClient = ftpClient;
	}

	private Long timeStampVerificacaoInterna = Long.valueOf(0L);
	private String dirThread;
	private boolean flagRestart;

	public Long getTimeStampVerificacaoInterna()
	{
		return this.timeStampVerificacaoInterna;
	}

	public void setTimeStampVerificacaoInterna(Long timeStampVerificacaoInterna)
	{
		this.timeStampVerificacaoInterna = timeStampVerificacaoInterna;
	}

	public String getDirThread()
	{
		return this.dirThread;
	}

	public void setDirThread(String dirThread)
	{
		this.dirThread = dirThread;
	}

	public boolean isFlagRestart()
	{
		return this.flagRestart;
	}

	public void setFlagRestart(boolean flagRestart)
	{
		this.flagRestart = flagRestart;
	}

	public void run()
	{
		this.contadorArquivosEnviados = 1;
		try
		{
			for (;;)
			{
				if (this.flagRestart) {
					this.flagRestart = false;
				}
				this.timeStampVerificacaoInterna = Long.valueOf(System.currentTimeMillis());

				System.out.println("Iniciando " + getName());
				try
				{

					String user = this.props.getProperty("user.servidor");
					String host = this.props.getProperty("host");
					String senhaServidor = this.props.getProperty("senha.servidor");
					if ((this.ftpClient == null) || (!this.ftpClient.isConnected()))
					{
						if (this.ftpClient == null) {
							this.ftpClient = new FTPClient();
						}
						try
						{
							this.ftpClient.connect(host, 21);
						}
						catch (Exception e)
						{
							throw new ConnectException(
									"Não foi possivel se conectar ao servidor FTP \n" + 
											e);
						}
						if (FTPReply.isPositiveCompletion(this.ftpClient.getReplyCode())) {
							this.ftpClient.login(user, senhaServidor);
						} else {
							throw new ConnectException(
									"Não foi possivel se conectar ao servidor FTP");
						}
					}
					for (;;)
					{
						if (this.dirThread != null)
						{
							getFileProcess(this.props);
						}
						else
						{
							getFileProcess(this.props);
						}
						Thread.sleep(2000L);
					}
				}
				catch (ConnectException e)
				{
					setFlagRestart(true);
				}
				catch (Exception e)
				{
					setFlagRestart(true);
				}
				finally
				{
					if ((this.ftpClient != null) && (this.ftpClient.isConnected())) {
						try
						{
							this.ftpClient.disconnect();
						}
						catch (IOException e)
						{
						}
					}
				}
			}
		}
		catch (Exception e)
		{
		}
	}

	private void getFileProcess(Properties props)
			throws Exception
	{
		this.timeStampVerificacaoInterna = Long.valueOf(System.currentTimeMillis());

		File diretorioOrigem = new File(props.getProperty("diretorio.origem"));
		String diretorioBackup = props.getProperty("diretorio.backup");
		String diretorioFinal = props.getProperty("diretorio.final");

		File[] files = FileUtils.dirListByAscendingDate(diretorioOrigem);

		long time = System.currentTimeMillis();
		if ((files != null) && (files.length > 0))
		{
			File[] arrayOfFile1;
			int j = (arrayOfFile1 = files).length;
			for (int i = 0; i < j; i++)
			{
				File file = arrayOfFile1[i];

				this.timeStampVerificacaoInterna = Long.valueOf(System.currentTimeMillis());

				String nomeArquivo = file.getName();

				enviarArquivo(file, nomeArquivo, this.ftpClient, 
						diretorioBackup);

				moverArquivo(diretorioOrigem, nomeArquivo, diretorioFinal);
				if (System.currentTimeMillis() - time > 60000L) {
					break;
				}
			}
		}
	}

	public void moverArquivo(File diretorioOrigem, String nomeArquivo, String diretorioFinal)
			throws Exception
	{
		File arqIn = new File(diretorioOrigem.getPath() + "/" + nomeArquivo);
		File arqOut = null;
		if (nomeArquivo.endsWith(".txt"))
		{
			nomeArquivo = nomeArquivo.substring(0, nomeArquivo.length() - 4);
			arqOut = new File(diretorioFinal + "/" + nomeArquivo + ".txt");
		}
		if (arqOut.exists())
		{
			System.out.println("Arquivo já existe: " + arqOut.getName());
			arqOut.delete();
		}
		arqIn.renameTo(arqOut);
	}

	public void enviarArquivo(File file, String nomeArquivo, FTPClient ftpClient, String diretorioBackup)
			throws IOException, InterruptedException
	{

		Boolean ackComando = Boolean.valueOf(false);
		if (ftpClient != null)
		{
			String sizeArquivoEnviado = "";
			FileInputStream fis;
			do
			{
				fis = new FileInputStream(file);

				do
				{
					ftpClient.changeWorkingDirectory(diretorioBackup);

					System.out.println("Enviando Arquivo: " + nomeArquivo + " Para o diretorio " +  diretorioBackup + " Contador geral: " + this.contadorArquivosEnviados);

					ftpClient.storeFile(nomeArquivo, fis);
					if (ftpClient.getReplyCode() != 226) {
						System.out.println("Erro ao enviar o arquivo cod:" + 
								ftpClient.getReplyCode());
					}
					System.out.println("Arquivo: " + nomeArquivo + " Enviado para o diretorio " +  diretorioBackup + " Contador Geral: " + this.contadorArquivosEnviados);
				} while (ftpClient.getReplyCode() != 226);
				ackComando = Boolean.valueOf(ftpClient.doCommand("size", nomeArquivo));
				if (ackComando.booleanValue())
				{
					sizeArquivoEnviado = ftpClient.getReplyString();
					String[] retornoComando = sizeArquivoEnviado.split(" ");
					sizeArquivoEnviado = retornoComando[1];

					sizeArquivoEnviado = sizeArquivoEnviado.replaceAll(
							"(\\r|\\n|\\t)", "");
				}
				if ((!ackComando.booleanValue()) || 
						(Long.parseLong(sizeArquivoEnviado) < file.length()) || Long.parseLong(sizeArquivoEnviado) == 0.0) {
					System.out.println("Diretorio: " + 
							diretorioBackup + "\nNome Arquivo: " + 
							nomeArquivo + 
							" Tamanho local e enviado divergem: \nack:" + 
							ackComando + " \nTamanhoLocal:" + file.length() + 
							" \nTamanhoEnviado:" + sizeArquivoEnviado);
				}

			} while ((!ackComando.booleanValue()) || (
					Long.parseLong(sizeArquivoEnviado) < file.length()) || Long.parseLong(sizeArquivoEnviado) == 0.0);
			ftpClient.cdup();
			fis.close();

		}
		else
		{
			throw new FTPConnectionClosedException("FTPClient nao Instanciado!");
		}
		FileInputStream fis;
		this.contadorArquivosEnviados += 1;
	}




}
