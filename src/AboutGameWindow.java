import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.stream.Stream;

public class AboutGameWindow extends JDialog{


    public AboutGameWindow ( JFrame jFrame ) {
        super(jFrame, "About the Game", true);

        Font font = new Font(Font.SERIF, Font.PLAIN, 20);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont( font );

        //control's info to play de game ( in html view )
        JEditorPane gameControlsEditorPane = createEditorPane("AboutGame.html");
        gameControlsEditorPane.setEditable(false);
        tabbedPane.addTab("Controls", new JScrollPane(gameControlsEditorPane));

        //License game
        JTextArea licenseTextArea = new JTextArea(getTextFromFile("LICENSE"));
        licenseTextArea.setEditable(false);
        tabbedPane.addTab("License", new JScrollPane(licenseTextArea));

        //Feedback
        JPanel feedbackPanel = new JPanel(new BorderLayout());
        JLabel feedbackLabel = new JLabel("<html>" +
                "<center>" +
                "<p >If you want to send me a comment about the game,</p>"+
                "<p>please do it on my <b>website</b>. At the end of the webpage you will find a form to do it.</p>" +
                "<p>( There is a button below, just click on it to open the web browser )<p>"+
                "<p style=\"font-size:40;\"><b>Thank you</b><p>" +
                "</center>" +
                "</html>");
        feedbackLabel.setFont(font);
        feedbackPanel.add( feedbackLabel, BorderLayout.CENTER );

        //Button opens the website in the web browser
        JButton goToWebsiteButton = new JButton("Go to the website to leave a comment");
        goToWebsiteButton.setFont(font);
        goToWebsiteButton.addActionListener(e -> {

            if ( Desktop.isDesktopSupported() ){
                try {
                    Desktop.getDesktop().browse(new URI("https://paulzeta.wordpress.com/mis-proyectos/snake-game"));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }else{
                JOptionPane.showMessageDialog(AboutGameWindow.this, "Desktop is not supported", "Error", JOptionPane.ERROR_MESSAGE);
            }

        });

        feedbackPanel.add(goToWebsiteButton, BorderLayout.SOUTH );
        tabbedPane.addTab("Feedback", feedbackPanel);

        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

        add(tabbedPane, BorderLayout.CENTER );

        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setSize(550,400 );
        setResizable(false);


    }


    private JEditorPane createEditorPane( String fileName ){
        JEditorPane editorPane = new JEditorPane();

        try {
            editorPane.setPage(getClass().getResource(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return editorPane;
    }

    private String getTextFromFile(String fileName ){

        Path path = null;
        try {
            path = Paths.get(getClass().getClassLoader().getResource(fileName).toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        StringBuilder data = new StringBuilder();
        try( Stream<String> lines = Files.lines(path) ) {
            lines.forEach(line -> data.append(line).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        return data.toString();
    }



}
