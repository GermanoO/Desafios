package backup;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
	
	private static Properties props;
	private static InputStream inFile;
	private static String nameThreadGeral = "Thread Geral";
	private static String nameThreadColeta = "Thread Coletadora";

	public static void main(String[] args) throws IOException,
			InterruptedException {
		
		props = new Properties();
		
		inFile = new FileInputStream(
				(new File(
						"propriedades.properties"))
						.getAbsoluteFile());
		
		props.load(inFile);

		inFile.close();

		GetFile threadGeneralInterfaces = new GetFile(false, props);
		threadGeneralInterfaces.setName(nameThreadGeral);

		GetFile threadColeta = new GetFile(false, props);
		threadColeta.setName(nameThreadColeta);


		threadColeta.start();

		while (true) {
			try {
				if (threadGeneralInterfaces.isFlagRestart()) {
					threadGeneralInterfaces = new GetFile(false, props);
					threadGeneralInterfaces.start();
					threadGeneralInterfaces.setName(nameThreadGeral);
				}

				if (threadColeta.isFlagRestart()) {
					threadColeta = new GetFile(false, props);
					threadColeta.start();
					threadColeta.setName(nameThreadColeta);
				}


				validarReinicioThread(threadGeneralInterfaces);
				validarReinicioThread(threadColeta);
				
				Thread.sleep(60000);

			} catch (Exception e) {

				validarReinicioThread(threadGeneralInterfaces);
				validarReinicioThread(threadColeta);

				e.printStackTrace();
				
				Thread.sleep(60000);
			}

		}

	}

	private static void validarReinicioThread(GetFile thread)
			throws IOException, InterruptedException {
		if ((System.currentTimeMillis()
				- thread.getTimeStampVerificacaoInterna() > 300000)
				|| thread.isFlagRestart()) {
			System.out.println("Time Out " + thread.getName()
					+ "\n     Arquivos Enviados: " + thread.contadorArquivosEnviados
					+ "\n     Data Hora Conexao: " + thread.dataConexao
					+ "\n     Data Hora Ultimo Envio: " + thread.dataUltimoEnvio);

			if (thread.getFtpClient() != null) {

				System.out.println("Desconectando do FTP! " + thread.getName());
				thread.getFtpClient().disconnect();
			}

			Thread.interrupted();
			thread.setFlagRestart(true);

		}

	}
	
}
