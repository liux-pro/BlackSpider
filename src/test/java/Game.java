import java.awt.*;
import java.awt.image.BufferStrategy;
import javax.swing.*;

public class Game implements Runnable{

   final int WIDTH = 640;
   final int HEIGHT = 480;

   JFrame frame;
   Canvas canvas;
   BufferStrategy bufferStrategy;

   boolean running = false;

   public Game(){
      frame = new JFrame("Prototyping");

      JPanel panel = (JPanel) frame.getContentPane();
      panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
      panel.setLayout(new GridLayout());


      canvas = new Canvas();
      //canvas.setBounds(0, 0, WIDTH, HEIGHT);
      canvas.setIgnoreRepaint(true);

      panel.add(canvas);

      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.pack();
      frame.setResizable(true);
      frame.setVisible(true);

      canvas.createBufferStrategy(2); 
      bufferStrategy = canvas.getBufferStrategy();

      canvas.requestFocus();
   }

   public void run(){
      running = true;
      while(running)         
         render();
   }

   private void render() {
      Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
      g.clearRect(0, 0, WIDTH, HEIGHT);
      render(g);
      g.dispose();
      bufferStrategy.show();
   }

   protected void render(Graphics2D g){
      g.setColor(Color.GRAY);
      g.fillRect(0, 0, WIDTH, HEIGHT);

      g.setColor(Color.BLUE);
      g.fillRect(100, 0, 200, 200);
   }

   public static void main(String [] args){
      Game game = new Game();
      new Thread(game).start();
   }
}