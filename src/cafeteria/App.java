package cafeteria;

import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class App {

    public static void main(String[] args) {

        Locale.setDefault(Locale.forLanguageTag("pt-BR"));

        try {


            // Tema multiplataforma
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");


        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Make sure we have nice window decorations.
                JFrame.setDefaultLookAndFeelDecorated(true);

                // Create and set up the window.
                TelaPrincipal frame = new TelaPrincipal();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // Display the window.
                frame.setVisible(true);
            }
        });
    }

}
