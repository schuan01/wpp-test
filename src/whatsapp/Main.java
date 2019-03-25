package whatsapp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import clases.Enviar;

//----------------- EXPLICACION DE LA LOGICA ------------------------------
/*
1. Primero hay que "escuchar" si de los chats de la izquierda, hay alguno con bolita verde de nuevo mensaje.
Si no hay, seguimos corriendo hasta encontrar.
2. Cuando se encuentra el mensaje nuevo, hay que  darle click programaticamente y esperar a que cargue el chat un rato(unos 3 segundos).
3. Luego de esperar, hay que leer el ultimo mensaje, teniendo en cuenta la fecha de hoy y esas cosas.
4. Al leerlo, respondemos y seguimos el esperando para el punto 1.
//-------------------------------------------------------------------------
*/

public class Main {

	//GLOBALES
	public static WebDriver driver;
	public static Map<String, String>  codigosCSS;
	public static Date fecha_hora_sistema;

	public static void main(String[] args) {

		try
		{
		
			driver = new ChromeDriver();

		    //Firefox's proxy driver executable is in a folder already
		    //  on the host system's PATH environment variable.
			driver.get("https://web.whatsapp.com");
			
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            fecha_hora_sistema = new Date();
			
			String mensaje_actual = "";
			String mensaje_actual_id = "";
			String mensaje_anterior_id = "";
			//String historial_comandos = array();
			boolean estaEjecutandoRespuesta = false;

			codigosCSS = new HashMap<String, String>();
			codigosCSS.put("campo_escribir","_2S1VP");
			//codigosCSS.put("burbuja_valor","copyable-text");
			codigosCSS.put("burbuja_valor", "Tkt2p");//Esta solo en bubujas con texto, 
			codigosCSS.put("burbuja_entrada","message-in");
			codigosCSS.put("burbuja_salida","message-out");
			codigosCSS.put("boton_adjuntar","//*[@id='main']/header/div[3]/div/div[2]/div");
			codigosCSS.put("boton_adjuntar_documento","//*[@id='main']/header/div[3]/div/div[2]/span/div/div/ul/li[3]/input");
			codigosCSS.put("boton_adjuntar_imagen","//*[@id='main']/header/div[3]/div/div[2]/span/div/div/ul/li[1]/input");
			codigosCSS.put("boton_enviar_adjunto","//*[@id='app']/div/div/div[1]/div[2]/span/div/span/div/div/div[2]/span[2]/div/div");
			codigosCSS.put("burbuja_hora","_3EFt_");
			codigosCSS.put("burbuja_fecha_hora","data-pre-plain-text");
			codigosCSS.put("burbuja_div_info","/div");
			codigosCSS.put("hay_mensaje_nuevo","_15G96");
			codigosCSS.put("div_chat","_2wP_Y");
			codigosCSS.put("div_chat_activo","_1f1zm");
			codigosCSS.put("div_ventana_chat","_2nmDZ");
			codigosCSS.put("nombre_chat","_1wjpf");
			codigosCSS.put("burbuja_para_emojis","alt");
			codigosCSS.put("todos_mensajes","vW7d1");

			Enviar.escribirLog("----------- INICIO: "+dateFormat.format(fecha_hora_sistema)+"  -------------- ", "MAIN", "");

			while(true)
			{
				try 
				{
					if(!estaEjecutandoRespuesta)
					{
						List<WebElement> divsALeer = escuchar_mensajes_nuevos();
						if(divsALeer.size() > 0)
						{
							
							Enviar.escribirLog("Total de mensajes nuevos encontrados: "+divsALeer.size(),"MAIN","");
							for (WebElement msj : divsALeer) 
							{
								
								WebElement contacto = msj.findElement(By.className(codigosCSS.get("nombre_chat")));
								String numero_contacto = contacto.getText();

								//Hago click en el div
								estaEjecutandoRespuesta = true;
								new Actions(driver).moveToElement(msj).click().perform();
								

								//Comparamos el mensaje activo contra el que le hicimos click
								WebElement msj_activo = driver.findElement(By.className(codigosCSS.get("div_chat_activo")));
								WebElement temp_contacto = msj_activo.findElement(By.className(codigosCSS.get("nombre_chat")));
								String temp_numero = temp_contacto.getText();

								//Si son distintos
								while (!numero_contacto.equals(temp_numero) ) {

									Enviar.escribirLog("El numero de contacto "+numero_contacto+" es diferente al seleccionado en el click "+temp_numero+" (SE VA A REINTENTAR)","MAIN",numero_contacto);
									new Actions(driver).moveToElement(msj).click().perform();

									msj_activo = driver.findElement(By.className(codigosCSS.get("div_chat_activo")));
									temp_contacto = msj_activo.findElement(By.className(codigosCSS.get("nombre_chat")));
									temp_numero = temp_contacto.getText();
									
								}

								WebDriverWait wait = new WebDriverWait(driver, 10);
								wait.until(ExpectedConditions.presenceOfElementLocated(By.className(codigosCSS.get("div_ventana_chat"))));
								mensaje_actual = get_mensajes();//Obtengo los mensajes de la ventana actual
								if(mensaje_actual != null && !mensaje_actual.equals(""))
								{
									DateFormat form = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
									String time_id = form.format(new Date());
									mensaje_actual_id = time_id;
									if(!mensaje_anterior_id.equals(mensaje_actual_id) )
									{
										
										mensaje_anterior_id = mensaje_actual_id;
										Enviar.escribirLog("Mensaje leido: "+mensaje_actual,"MAIN",numero_contacto);

										ejecuta_bot(mensaje_actual, numero_contacto);
										estaEjecutandoRespuesta = false;
										continue;
									}
									else
									{
										estaEjecutandoRespuesta = false;
									}

								}
								else
								{
									Enviar.escribirLog("No encontro msj valido(enviaron una imagen o un emoji o era repetido)","MAIN",numero_contacto);
									estaEjecutandoRespuesta = false;
								}
						
								
							}
						}
					}
					
				} catch(WebDriverException ex)
				{
					
				}
				catch(Exception e)
				{
					Enviar.escribirLog(e.getMessage(),"MAIN","");
					throw e;
				}
			}
			
		   
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}

	}

	private static List<WebElement>  escuchar_mensajes_nuevos() throws Exception
	{
		List<WebElement> arrayDivsPelotitas = new ArrayList<WebElement>();
		List<WebElement> div_mensaje = driver.findElements(By.className(codigosCSS.get("div_chat")));

		//Recorro todos los DIV de la izquierda
		for (WebElement div : div_mensaje) {
			
			//Del DIV actual, veo si tiene la pelotita verde
			try {

				//Si no lo encuentra, tira exception
				div.findElement(By.className(codigosCSS.get("hay_mensaje_nuevo")));
				arrayDivsPelotitas.add(div);
				
			} catch (WebDriverException e1) {
				continue;
			}
			catch (Exception e) {
				throw e;
			}
		}
		
		return arrayDivsPelotitas;
	}

	private static String get_mensajes()
	{
		try {
			List<WebElement> todos_mensajes = driver.findElements(By.className(codigosCSS.get("todos_mensajes")));//Recibidos y enviados
			Map<Integer, Map<String,String>> temporal = new HashMap<Integer, Map<String,String>>();
			int contador = 0;
			
			for (WebElement mensaje : todos_mensajes) {//Recorro cada linea de mensaje
				//List<WebElement> texto_msj = mensaje.findElements(By.className(codigosCSS.get("burbuja_valor")));
				List<WebElement> hay_valor_in = mensaje.findElements(By.className(codigosCSS.get("burbuja_entrada")));
				List<WebElement> hay_valor_out = mensaje.findElements(By.className(codigosCSS.get("burbuja_salida")));
				if(hay_valor_in.size() > 0 || hay_valor_out.size() > 0)
				{
					List<WebElement> msjs = mensaje.findElements(By.className(codigosCSS.get("burbuja_valor")));
					if(msjs.size() == 0)//Es vacio cuando hay una imagen
					{
						continue;//Si es vacio, reviso la siguiente linea nomas
					}
					WebElement msj = msjs.get(0);
					WebElement burbuja_info = msj.findElement(By.xpath("div"));
					//for (WebElement msj : texto_msj) {//Recorro todos los burbuja valor validos(en teoria se queda con el ultimo)
	
						String texto = burbuja_info.getText();
						String valor = burbuja_info.getAttribute(codigosCSS.get("burbuja_fecha_hora"));
						if(valor != null)
						{
							final Matcher m = Pattern.compile("^\\[(\\d*\\:\\d*)\\,\\s(\\d+\\/\\d+\\/\\d+)(.*)\\]").matcher(valor);
							if (m.find())
							{
								String hora_mensaje = m.group(1);//formato hh:mm
								String fecha_mensaje = m.group(2);//Formato dd/mm/yyyy
	
								DateFormat formato = new SimpleDateFormat("d/M/yyyy HH:mm");
								Date fecha_real_mensaje = formato.parse(fecha_mensaje+" "+hora_mensaje);
								Calendar cal = Calendar.getInstance();  
								cal.setTime(fecha_hora_sistema); 
								cal.set(Calendar.SECOND, 0);  
								cal.set(Calendar.MILLISECOND, 0);
								
								if(fecha_real_mensaje.equals(cal.getTime()) || fecha_real_mensaje.after(cal.getTime()))
								{
									if(hay_valor_in.size() > 0)
									{
										Map<String,String> tmp = new HashMap<String,String>();
										tmp.put("cliente", texto);
										temporal.put(contador,tmp);
										contador++;
									}
									else if(hay_valor_out.size() > 0)
									{
										Map<String,String> tmp = new HashMap<String,String>();
										tmp.put("servidor", texto);
										temporal.put(contador,tmp);
										contador++;
									}
								}
								
	
							}
							else
							{
								
							}
						}
	
				}
					

					
				//}
				
			}

			//Cuando termino de recorrer, me quedo con el ultimo mensaje
			if(temporal.size() > 0)
			{
				int ultimo = temporal.size() - 1;//Ultimo indice
				Map<String,String> claveOrigen = temporal.get(ultimo);
				if(claveOrigen.containsKey("cliente"))
				{
					return claveOrigen.get("cliente");
				}
			}
		} catch (Exception e) {
			Enviar.escribirLog(e.getMessage(), "Exception get_mensajes()", "");
		}
		

		return "";//Si no logra devolver un mensaje
	}

	private static void ejecuta_bot(String nombreComando, String numero)
	{
		if(nombreComando.toLowerCase().matches("(.*)hola(.*)"))
		{
			Enviar.decir_hola(driver, codigosCSS, nombreComando, numero);
		}
		else if(nombreComando.toLowerCase().matches("(.*)adjunto(.*)"))
		{
			Enviar.enviar_adjunto(driver, codigosCSS, nombreComando, numero);
		}
	}

}
