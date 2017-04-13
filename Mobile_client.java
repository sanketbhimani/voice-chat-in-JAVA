import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class Mobile_client {
   
    volatile static boolean running = true;
    public static void main(String[] args) {
        Socket socket;
        try {
            socket = new Socket("192.168.2.18",123);
        
        Thread recive = new Thread(()->{
            AudioFormat format;
            DataLine.Info info;
            SourceDataLine sline;
            
            byte[] buffer1;
            
                
                InputStream in;
            try {
                in = socket.getInputStream();
            
                int count;
                byte[] buffer = new byte[1000000];
                format = new AudioFormat(44100,16,2,true,true);
                info = new DataLine.Info(SourceDataLine.class,format);
                sline = (SourceDataLine)AudioSystem.getLine(info);
                sline.open(format);
                sline.start();
                while((count=in.read(buffer,0,buffer.length))!=-1){
                    if(count>0){
                        System.out.println(count+" ");
                        sline.write(buffer, 0, count);
                        buffer = new byte[1000000];
                    }               
                }
                sline.stop();
                sline.close();
                } catch (IOException ex) {
                    System.out.println("recive i/o:"+ex);
                Logger.getLogger(Mobile_client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (LineUnavailableException ex) {
                System.out.println("recive line:"+ex);
                Logger.getLogger(Mobile_client.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        });
        Thread send = new Thread(() -> {
           
                OutputStream oos;
            try {
                oos = socket.getOutputStream();
            
                
                TargetDataLine line;
                AudioFormat format = new AudioFormat(44100,16,2,true,true);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class,format);
                if(!AudioSystem.isLineSupported(info)){
                    System.err.println("Your hardware is not supported");
                    System.exit(1);
                }
                line = (TargetDataLine)AudioSystem.getLine(info);
                line.open(format);
                line.start();
                System.out.println("Start Capturing...");
                byte buffer[] = new byte[(int)format.getSampleRate()*format.getFrameSize()];
                while(running){
                    int count = line.read(buffer, 0, buffer.length);
                    if(count > 0){
                        oos.write(buffer, 0, count);
                    }
                }
                } catch (IOException ex) {
                    System.out.println("send i/o:"+ex);
                Logger.getLogger(Mobile_client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (LineUnavailableException ex) {
                System.out.println("send line:"+ex);
                Logger.getLogger(Mobile_client.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        });
        recive.start();
        send.start();
        } catch (IOException ex) {
            System.out.println("socket:" +ex);
        }
        
    }
}
	