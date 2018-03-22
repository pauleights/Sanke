import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Window extends JFrame {

    //    private NewGame newGameAction;
    private AboutGameWindow aboutGameWindow;
    //GAME
    private SnakeGame snakeGame;//the full game


    public Window (){
        super("Paul's snake game");

        snakeGame = new SnakeGame();//full game

        //menus's characteristics
        Font menusFont = new Font(Font.SERIF, Font.PLAIN, 18);

        //Create the menu bar
        JMenuBar menuBar = new JMenuBar();

        //Build the first menu Game in the menu bar
        JMenu gameMenu = new JMenu("Game");
        gameMenu.setMnemonic(KeyEvent.VK_G);
        gameMenu.setFont(menusFont);
        menuBar.add(gameMenu);

        //Submenu to show the game level section 
        JMenu gameLevelSubMenu = new JMenu("New Game");
        gameLevelSubMenu.setFont(menusFont);
        gameMenu.add(gameLevelSubMenu);

        //Game level selection SLOW , MEDIUM , FAST
        GameLevelActionListener gameLevelActionListener = new GameLevelActionListener();

        JRadioButtonMenuItem easyModeRadio = new JRadioButtonMenuItem("Easy");
        easyModeRadio.setFont(menusFont);
        easyModeRadio.addActionListener( gameLevelActionListener );

        JRadioButtonMenuItem mediumModeRadio = new JRadioButtonMenuItem("Medium");
        mediumModeRadio.setSelected(true);
        mediumModeRadio.setFont(menusFont);
        mediumModeRadio.addActionListener( gameLevelActionListener );

        JRadioButtonMenuItem hardModeRadio = new JRadioButtonMenuItem("Hard");
        hardModeRadio.setFont(menusFont);
        hardModeRadio.addActionListener( gameLevelActionListener );

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(easyModeRadio);
        buttonGroup.add(mediumModeRadio);
        buttonGroup.add(hardModeRadio);

        gameLevelSubMenu.add(easyModeRadio);
        gameLevelSubMenu.add(mediumModeRadio);
        gameLevelSubMenu.add(hardModeRadio);

        //menu item to exit the game 
        JMenuItem exitGameMenuItem = new JMenuItem("Exit");
        exitGameMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.ALT_DOWN_MASK));
        exitGameMenuItem.setFont(menusFont);
        exitGameMenuItem.addActionListener( e -> System.exit(0));
        gameMenu.add(exitGameMenuItem);

        
        //Build second menu Designer in the menu bar
        JMenu designerMenu = new JMenu("Designer");
        designerMenu.setMnemonic(KeyEvent.VK_D);
        designerMenu.setFont(menusFont);
        menuBar.add(designerMenu);

        //Submenu to choose the shape of snake ( two options ) : round , square
        JMenu typeSnakeSubMenu = new JMenu("Type");
        typeSnakeSubMenu.setFont(menusFont);

        JRadioButtonMenuItem roundTypeRadio = new JRadioButtonMenuItem("Round");
        roundTypeRadio.setFont(menusFont);
        roundTypeRadio.setSelected(true);
        roundTypeRadio.addActionListener(new TypeOfSnakeActionListener());

        JRadioButtonMenuItem squareTypeRadio = new JRadioButtonMenuItem("Square");
        squareTypeRadio.setFont(menusFont);
        squareTypeRadio.addActionListener(new TypeOfSnakeActionListener());

        buttonGroup = new ButtonGroup();
        buttonGroup.add(roundTypeRadio);
        buttonGroup.add(squareTypeRadio);

        typeSnakeSubMenu.add(roundTypeRadio);
        typeSnakeSubMenu.add(squareTypeRadio);

        designerMenu.add(typeSnakeSubMenu);

        //CheckBox to change the fill options of the snake ( two options ) : filled, not filled
        JCheckBoxMenuItem filledTypeCheckBox = new JCheckBoxMenuItem("Filled");
        filledTypeCheckBox.setFont(menusFont);
        filledTypeCheckBox.setSelected(true);
        filledTypeCheckBox.addItemListener( e -> snakeGame.setSnakeFilled(filledTypeCheckBox.isSelected()));
        designerMenu.add(filledTypeCheckBox);

        //MenuItem to change the snake's color
        JMenuItem changeSnakeColorMenuItem = new JMenuItem("Change color");
        changeSnakeColorMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        changeSnakeColorMenuItem.setFont(menusFont);
        changeSnakeColorMenuItem.addActionListener( e -> snakeGame.changeSnakeColor() );
        designerMenu.add(changeSnakeColorMenuItem);


        //Build third menu About in the menu bar
        JMenu aboutMenu = new JMenu("About");
        aboutMenu.setMnemonic(KeyEvent.VK_A);
        aboutMenu.setFont(menusFont);
        menuBar.add(aboutMenu);

        //MenuItem to open a new window with the game's info
        JMenuItem gameAboutMenuItem = new JMenuItem("About the game");
        gameAboutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        gameAboutMenuItem.setFont(menusFont);

        //Game's info window
        aboutGameWindow = new AboutGameWindow(this);
        aboutGameWindow.setVisible(false);
        aboutGameWindow.setLocationRelativeTo(this);

        gameAboutMenuItem.addActionListener( e -> aboutGameWindow.setVisible(true));
        aboutMenu.add(gameAboutMenuItem);


        JMenuItem developerMenuItem = new JMenuItem("Developer...");
        developerMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
        developerMenuItem.setFont(menusFont);
        developerMenuItem.addActionListener(e -> {

            if ( Desktop.isDesktopSupported() ){

                try {
                    Desktop.getDesktop().browse( new URI("https://paulzeta.wordpress.com/contact/"));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }

            }else{
                JOptionPane.showMessageDialog(Window.this, "Desktop is not supported", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
                
        aboutMenu.add(developerMenuItem);


        //Build fourth menu Sound in the menu bar
        JMenu soundMenu = new JMenu("Sound");
        soundMenu.setFont(menusFont);
        soundMenu.setMnemonic(KeyEvent.VK_S);
        if ( snakeGame.isSoundSupported() ){ menuBar.add(soundMenu); }

        JCheckBoxMenuItem activateSoundCheckBox = new JCheckBoxMenuItem("Sound Effects");
        activateSoundCheckBox.setSelected(true);
        activateSoundCheckBox.setFont(menusFont);
        activateSoundCheckBox.addItemListener( e -> snakeGame.setSupportsSound(activateSoundCheckBox.isSelected()));

        soundMenu.add(activateSoundCheckBox);


        setJMenuBar(menuBar);

        add(snakeGame, BorderLayout.CENTER);
        setResizable(false);


    }


    private class TypeOfSnakeActionListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            switch (e.getActionCommand()){
                case "Round":
                    snakeGame.setShapeOfSnake( Snake.ROUND_SHAPE );
                    break;
                case "Square":
                    snakeGame.setShapeOfSnake( Snake.SQUARE_SHAPE );
                    break;
            }
        }
    }

    private class GameLevelActionListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            switch (e.getActionCommand()){
                case "Easy":
                     snakeGame.getTimerGame().setDelay(SnakeGame.EASY_MODE);
                     snakeGame.restarGame();
                     break;
                 case "Medium":
                     snakeGame.getTimerGame().setDelay(SnakeGame.MEDIUM_MODE);
                     snakeGame.restarGame();
                     break;
                 case "Hard":
                     snakeGame.getTimerGame().setDelay(SnakeGame.HARD_MODE);
                     snakeGame.restarGame();
                     break;
             }

        }
    }


}//end of the class
