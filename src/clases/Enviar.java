package clases;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class Enviar {

    public static void enviar_mensaje(WebDriver driver,String msg, Map<String,String> codigosCSS, String nombreComando, String numero)
    {
        try
        {
            escribirLog("Respondiendo a: " + nombreComando,"enviar_mensaje",numero);
            WebElement whatsapp_msg = driver.findElement(By.className(codigosCSS.get("campo_escribir")));
            whatsapp_msg.sendKeys(msg);
            whatsapp_msg.sendKeys(Keys.ENTER);
            escribirLog("Respuesta Exitosa","enviar_mensaje",numero);

        }
        catch(Exception ex)
        {
            escribirLog(ex.getMessage(),"enviar_mensaje Exception","");

        }
    }

    public static void enviar_mensaje_ya_escrito(WebDriver driver,Map<String,String> codigosCSS,String numero)
    {
        try
        {
            WebDriverWait wait = new WebDriverWait(driver, 10);// 10 SEG MAXIMO
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(codigosCSS.get("campo_escribir"))));
            WebElement whatsapp_msg = driver.findElement(By.className(codigosCSS.get("campo_escribir")));
            whatsapp_msg.sendKeys(Keys.ENTER);
            escribirLog("Envio de mensaje Exitoso","enviar_mensaje_ya_escrito",numero);

        }
        catch(Exception ex)
        {
            escribirLog(ex.getMessage(),"enviar_mensaje_ya_escrito Exception","");

        }
    }

    public static void enviar_adjunto(WebDriver driver,Map<String,String> codigosCSS, String nombreComando, String numero)
    {
        String msg = "Enviando adjunto";
        escribirLog(msg,"enviar_adjunto",numero);

        enviar_mensaje(driver, msg, codigosCSS, nombreComando, numero);

        //Busca y presiona el boton adjuntar
        String attach_xpath = codigosCSS.get("boton_adjuntar");
        WebElement attach_btn = driver.findElement(By.xpath(attach_xpath));
        attach_btn.click();

        
        escribirLog("Adjutando documento","enviar_adjunto",numero);

        String attach_type_xpath = codigosCSS.get("boton_adjuntar_imagen");
        WebElement attach_img_btn = driver.findElement(By.xpath(attach_type_xpath));//ADJUNTO DOCUMENTO
        attach_img_btn.sendKeys("C:/Users/jvolpe/Documents/PHP/php-webdriver-community/php-webdriver-community/prueba.jpg");
        escribirLog("Adjunto exitoso del documento","enviar_adjunto",numero);

        escribirLog("Esperando a que cargue el boton de Enviar","enviar_adjunto",numero);
        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement send_button = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(codigosCSS.get("boton_enviar_adjunto"))));      
        escribirLog("Boton cargado","enviar_adjunto",numero);
        escribirLog("Enviando","enviar_adjunto",numero);
        send_button.click();
        escribirLog("Envio exitoso de adjunto","enviar_adjunto",numero);

        attach_btn = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(attach_xpath)));    
        escribirLog("Cerrando Menu","enviar_adjunto",numero);
        attach_btn.click();
        escribirLog("Cerrado correctamente","enviar_adjunto",numero);

    }

    public static void enviar_adjunto_archivo(WebDriver driver,Map<String,String> codigosCSS, String nombreComando, String numero)
    {
        String msg = "Enviando adjunto archivo";
        escribirLog(msg,"enviar_adjunto_archivo",numero);

        enviar_mensaje(driver, msg, codigosCSS, nombreComando, numero);

        //Busca y presiona el boton adjuntar
        String attach_xpath = codigosCSS.get("boton_adjuntar");
        WebElement attach_btn = driver.findElement(By.xpath(attach_xpath));
        attach_btn.click();

        
        

        String attach_type_xpath = codigosCSS.get("boton_adjuntar_documento");
        WebElement attach_img_btn = driver.findElement(By.xpath(attach_type_xpath));//ADJUNTO DOCUMENTO
        String ruta_documento = "C:/Users/jvolpe/Documents/PHP/php-webdriver-community/php-webdriver-community/prueba.jpg";
        
        escribirLog("Adjutando documento: " + ruta_documento,"enviar_adjunto_archivo",numero);
        attach_img_btn.sendKeys(ruta_documento);
        escribirLog("Adjunto exitoso del documento","enviar_adjunto_archivo",numero);

        escribirLog("Esperando a que cargue el boton de Enviar","enviar_adjunto_archivo",numero);
        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement send_button = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(codigosCSS.get("boton_enviar_adjunto"))));      
        escribirLog("Boton cargado","enviar_adjunto_archivo",numero);
        escribirLog("Enviando","enviar_adjunto_archivo",numero);
        send_button.click();
        escribirLog("Envio exitoso de adjunto","enviar_adjunto_archivo",numero);

        attach_btn = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(attach_xpath)));    
        escribirLog("Cerrando Menu","enviar_adjunto_archivo",numero);
        attach_btn.click();
        escribirLog("Cerrado correctamente","enviar_adjunto_archivo",numero);

    }

    public static void decir_hola(WebDriver driver,Map<String,String> codigosCSS, String nombreComando, String numero)
    {
        String msg = "Hola, bienvenido al Chat Bot";
        enviar_mensaje(driver, msg, codigosCSS, nombreComando, numero);
    }

    public static void contestar_puto(WebDriver driver,Map<String,String> codigosCSS, String nombreComando, String numero)
    {
        String msg = "Por favor no insultar a una inteligencia superior";
        enviar_mensaje(driver, msg, codigosCSS, nombreComando, numero);
    }

    public static void escribirLog(String msj,String metodo, String numero)
    {
        try
        {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            String armado = dateFormat.format(date)+"::"+"Metodo("+metodo+")::Numero_Contacto("+numero+")::"+msj+"\n";            
            BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/jvolpe/eclipse-oxygen/WhatsApp/log.txt",true));
            writer.append(armado);
            writer.close();
            System.out.print(armado);
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

}
