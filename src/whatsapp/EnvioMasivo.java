package whatsapp;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;


import clases.Enviar;

public class EnvioMasivo {

	// GLOBALES
	public static WebDriver driver;
	public static Map<String, String> codigosCSS;
	public static Date fecha_hora_sistema;

	public static void main(String[] args) {

		try {
			codigosCSS = new HashMap<String, String>();
			codigosCSS.put("campo_escribir", "_2S1VP");
			// codigosCSS.put("burbuja_valor","copyable-text");//En varias veces por burbuja,
			codigosCSS.put("burbuja_valor", "Tkt2p");// Esta solo en burbujas con texto,
			codigosCSS.put("burbuja_entrada", "message-in");
			codigosCSS.put("burbuja_salida", "message-out");
			codigosCSS.put("boton_adjuntar", "//*[@id='main']/header/div[3]/div/div[2]/div");
			codigosCSS.put("boton_adjuntar_documento","//*[@id='main']/header/div[3]/div/div[2]/span/div/div/ul/li[3]/input");
			codigosCSS.put("boton_adjuntar_imagen","//*[@id='main']/header/div[3]/div/div[2]/span/div/div/ul/li[1]/input");
			codigosCSS.put("boton_enviar_adjunto","//*[@id='app']/div/div/div[1]/div[2]/span/div/span/div/div/div[2]/span[2]/div/div");
			codigosCSS.put("burbuja_hora", "_3EFt_");
			codigosCSS.put("burbuja_fecha_hora", "data-pre-plain-text");
			codigosCSS.put("burbuja_div_info", "/div");
			codigosCSS.put("hay_mensaje_nuevo", "_15G96");
			codigosCSS.put("div_chat", "_2wP_Y");
			codigosCSS.put("div_chat_activo", "_1f1zm");
			codigosCSS.put("div_ventana_chat", "_2nmDZ");
			codigosCSS.put("nombre_chat", "_1wjpf");
			codigosCSS.put("burbuja_para_emojis", "alt");
			codigosCSS.put("todos_mensajes", "vW7d1");
			codigosCSS.put("div_adjunto_archivo", "_1vKRe");
			codigosCSS.put("div_checks_visto", "_32uRw");
			codigosCSS.put("data_span_div_checks", "data-icon");// EL primer span en el div check
			// KYpDv DIV IMAGEN ENVIADA
			// -> _32uRw DIV DE LOS CHECKS
			// -> span data-icon="msg-dblcheck-light" msg-check

			// PRIMERO ABRO EL NAVEGADOR PARA LEER EL QR CON LA CAMARA
			driver = new ChromeDriver();
			driver.get("https://web.whatsapp.com");

			// ESPERO A QUE EL SEÑOR USE LA CAMARA Y APAREZCA EL DIV CON ESTE CSS CLASS
			WebDriverWait wait = new WebDriverWait(driver, 60);// 1 MINUTO MAXIMO
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("_3q4NP")));

			String csvFile = "C:/Users/jvolpe/eclipse-oxygen/WhatsApp/enviar.txt";
			BufferedReader br = null;
			String line = "";
			String separador = ";";
			

			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            fecha_hora_sistema = new Date();
			Enviar.escribirLog("------- INICIO ENVIO MASIVO: "+dateFormat.format(fecha_hora_sistema)+"  --------- ", "MAIN ENVIO MASIVO", "");

			try {

				br = new BufferedReader(new FileReader(csvFile));
				while ((line = br.readLine()) != null) {

					String[] linea = line.split(separador);
					Enviar.escribirLog("Leyendo linea " + line, "MAIN ENVIO MASIVO", "");
					String armoURL = "https://api.whatsapp.com/send?phone=" + linea[0] + "&text=" + linea[1];

					Enviar.escribirLog("Accediendo al sitio ", "MAIN ENVIO MASIVO", linea[0]);
					driver.get(armoURL);

					Enviar.escribirLog("Esperando el boton enviar ", "MAIN ENVIO MASIVO", linea[0]);
					WebElement boton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("action-button")));
					boton.click();
					Enviar.escribirLog("Boton enviar clickeado ", "MAIN ENVIO MASIVO", linea[0]);

					Enviar.escribirLog("Esperando hasta que aparezcan los chats ", "MAIN ENVIO MASIVO", linea[0]);
					wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("_3q4NP")));// ESPERO HASTA QUE APAREZCA EL CHAT Y ESO
					Enviar.escribirLog("Aparecieron los chats ", "MAIN ENVIO MASIVO", linea[0]);

					// Envio el mensaje que escribimos en la URL
					Enviar.escribirLog("Empezando proceso enviar mensaje ya escrito ", "MAIN ENVIO MASIVO", linea[0]);
					Enviar.enviar_mensaje_ya_escrito(driver, codigosCSS, linea[0]);

					// Por ultimo, mando el adjunto
					Enviar.escribirLog("Empezando proceso enviar adjunto ", "MAIN ENVIO MASIVO", linea[0]);
					Enviar.enviar_adjunto_archivo(driver, codigosCSS, "Envio Masivo", linea[0]);
					Enviar.escribirLog("Proceso enviar adjunto termino correctamente ", "MAIN ENVIO MASIVO", linea[0]);

					int esperar = 1;
					Enviar.escribirLog("Esperando "+ esperar+" segundos para terminar esta lina", "MAIN ENVIO MASIVO", linea[0]);
					WebDriverWait tempWait = new WebDriverWait(driver, esperar); // define local/temp wait only for the "sleep"
					try {
						tempWait.until(ExpectedConditions.jsReturnsValue("algorandom")); // condition you are certain won't be true
					}
					catch (TimeoutException t) {
						//next; // catch the exception and continue the code
					}

					Enviar.escribirLog("Espera terminada ", "MAIN ENVIO MASIVO", linea[0]);
					
					
					Enviar.escribirLog("Obteniendo ultimos mensajes ", "MAIN ENVIO MASIVO", linea[0]);
					WebElement mensaje = get_mensajes_adjunto();
					if(mensaje != null)
					{
						Enviar.escribirLog("Ultimo mensaje obtenido correctamente ", "MAIN ENVIO MASIVO", linea[0]);

						WebElement div_checks = mensaje.findElement(By.className(codigosCSS.get("div_checks_visto")));
						WebElement span_siguiente = div_checks.findElement(By.xpath("span"));
						

						//Espero hasta que el valor sea chek o doble check en el mensaje
						Enviar.escribirLog("Esperando a que aparezcan los checks en el ultimo mensaje", "MAIN ENVIO MASIVO", linea[0]);
						wait.until(ExpectedConditions.attributeContains(span_siguiente, codigosCSS.get("data_span_div_checks"), "check"));
						Enviar.escribirLog("Aparecio el check en el ultimo mensaje", "MAIN ENVIO MASIVO", linea[0]);

						if(isAlertPresent())
						{
							Enviar.escribirLog("Se encontro una alerta, se le da aceptar ", "MAIN ENVIO MASIVO", linea[0]);
							Alert alert = driver.switchTo().alert();
							//Will Click on OK button.
							alert.accept();
						}

						Enviar.escribirLog("ENVIO DE ADJUNTO AL NUMERO "+linea[0]+" FUE UN EXITO", "MAIN ENVIO MASIVO", linea[0]);
					}
					else
					{
						Enviar.escribirLog("No se encontro mensaje valido, algo salio mal ", "MAIN ENVIO MASIVO", linea[0]);
					}

					

				}

				//TERMINO OK
				Enviar.escribirLog("TODAS LAS LINEAS CONSUMIDAS CON EXITO", "MAIN ENVIO MASIVO","");

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		} catch (Exception e) {
			Enviar.escribirLog(e.getMessage(), "MAIN ENVIAR MASIVO", "");
		}

	}

	private static WebElement get_mensajes_adjunto() {
		try {
			List<WebElement> todos_mensajes = driver.findElements(By.className(codigosCSS.get("todos_mensajes")));// Recibidos
																													// y
																													// enviados
			Map<Integer, Map<String, WebElement>> temporal = new HashMap<Integer, Map<String, WebElement>>();
			int contador = 0;

			for (WebElement mensaje : todos_mensajes) {// Recorro cada linea de mensaje

				List<WebElement> hay_valor_in = mensaje.findElements(By.className(codigosCSS.get("burbuja_entrada")));
				List<WebElement> hay_valor_out = mensaje.findElements(By.className(codigosCSS.get("burbuja_salida")));
				if (hay_valor_in.size() > 0 || hay_valor_out.size() > 0) {
					List<WebElement> msjs = mensaje.findElements(By.className(codigosCSS.get("div_adjunto_archivo")));
					if (msjs.size() == 0)// Es vacio no es un adjunto
					{
						continue;// Si es vacio, reviso la siguiente linea nomas
					}

					if (hay_valor_in.size() > 0) {
						Map<String, WebElement> tmp = new HashMap<String, WebElement>();
						tmp.put("cliente", mensaje);
						temporal.put(contador, tmp);
						contador++;
					} else if (hay_valor_out.size() > 0) {
						Map<String, WebElement> tmp = new HashMap<String, WebElement>();
						tmp.put("servidor", mensaje);
						temporal.put(contador, tmp);
						contador++;
						
					}

				}

				// }

			}

			// Cuando termino de recorrer, me quedo con el ultimo mensaje
			if (temporal.size() > 0) 
			{
				
				int ultimo = temporal.size() - 1;// Ultimo indice
				Map<String, WebElement> claveOrigen = temporal.get(ultimo);
				if (claveOrigen.containsKey("servidor")) {
					return claveOrigen.get("servidor");
				}
			}
		} catch (Exception e) {
			Enviar.escribirLog(e.getMessage(), "Exception get_mensajes_adjunto()", "");
		}

		return null;// Si no logra devolver un mensaje
	}

	public static boolean isAlertPresent() 
	{ 
		try 
		{ 
			driver.switchTo().alert(); 
			return true; 
		}   
		catch (NoAlertPresentException Ex) 
		{ 
			return false; 
		}   
	}  
}
