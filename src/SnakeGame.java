import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Arc2D;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

public class SnakeGame extends JPanel {

    private Snake[] snakeBody;
    private Snake snakeHead;
    private Snake snakeFood;

    private Timer timerGame;
    private int shapeOfSnake;
    private boolean filledSnake;
    private Clip eatSoundClip;
    private Clip gameOverSoundClip;
    private boolean supportsSound;

    private enum Move { UP, DOWN, LEFT, RIGHT }
    private Move direction;

    public static final int EASY_MODE = 130;
    public static final int MEDIUM_MODE = 80;
    public static final int HARD_MODE = 50;

    private int score;

    private int sizeSnake;

    private Random random;

    private final int WINDOW_WIDTH = 600;
    private final int WINDOW_HEIGHT = 600;
    private final int DOT_SIZE = 20;//width and height for every dot

    private int headX, headY;
    private int foodX, foodY;


    private boolean moveMade;//it detects when the movement has been made from arrow keys
    private boolean inGame;//true when the user is playing
    private boolean pause;//true when the game is paused with the space bar
    private boolean gameOver;//true when the user lost

    private boolean flickeringLetters;//to make the tetters flick in the intro


    public SnakeGame (){

        setBackground( Color.DARK_GRAY );
        setPreferredSize( new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT) );

        random = new Random();

        shapeOfSnake = Snake.ROUND_SHAPE;
        filledSnake = true;

        //head
        snakeHead = new Snake (DOT_SIZE);
        snakeHead.setShape( shapeOfSnake );
        snakeHead.setFilled(filledSnake);
        //body
        snakeBody = new Snake[ WINDOW_WIDTH/DOT_SIZE *  WINDOW_HEIGHT/DOT_SIZE ];

        //food
        snakeFood = new Snake ( DOT_SIZE );
        snakeFood.setShape( shapeOfSnake );
        snakeFood.setFilled(filledSnake);
        restarSnakeIntro();

        inGame = false;
        gameOver = false;

        supportsSound = getSoundEffects();

        if ( !supportsSound){
            JOptionPane.showMessageDialog(SnakeGame.this, "There was an error when searching for sound resources,\nbut you can still play the game without sound. \nHave fun", "Sound not found" , JOptionPane.ERROR_MESSAGE);
        }

        timerGame = new Timer( MEDIUM_MODE , new TimerHandler() );
        timerGame.start();

        //key bindings//////////////////////
        //Getting keyboard keys for the game
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,0), "up");
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0),"down");
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,0), "left");
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,0),"right");
        getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "enterKey");
        getInputMap().put(KeyStroke.getKeyStroke("SPACE"),"spaceKey");

        //Specifying actions for the keys
        getActionMap().put("up", new MoveUp());
        getActionMap().put("down", new MoveDown());
        getActionMap().put("left", new MoveLeft());
        getActionMap().put("right", new MoveRight());
        getActionMap().put("spaceKey", new SpaceKeyAction());
        getActionMap().put("enterKey", new EnterKeyAction());


    }//end of constructor

    private class TimerHandler implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            flickeringLetters = !flickeringLetters;

            if ( inGame ){//when user is playing

                moveSnake();//move the snake for the game

                if ( checkCollision() ){
                    if ( supportsSound ){
                        gameOverSoundClip.start();
                        gameOverSoundClip.setFramePosition(0);
                    }
                    gameOver = true;
                }

                if ( checkIfSnakeEats() ) {
                    if ( supportsSound ){
                        eatSoundClip.start();
                        eatSoundClip.setFramePosition(0);
                    }
                    relocateFood();
                }

                repaint();

            }else{//for the intro's game

                moveSnake();//move the snake for the intro animation

                if ( checkIfSnakeEats() ) {
                    relocateFood();
                }

                //when the snake is 10 points size the restar method makes it short again
                if ( sizeSnake == 10 ){
                    restarSnakeIntro();
                }

                repaint();
            }

        }
    }

    private class EnterKeyAction extends AbstractAction{

        @Override
        public void actionPerformed(ActionEvent e) {

            if ( inGame ){

                if ( gameOver ){
                    restarGame();//restar from game over
                }

            }else{

                restarGame();//restar from intro

            }

        }
    }

    private class SpaceKeyAction extends AbstractAction{

        @Override
        public void actionPerformed(ActionEvent e) {

            if ( inGame && !gameOver ){

                pause = !pause;

                if ( !timerGame.isRunning() ){
                    timerGame.start();
                }
            }


        }
    }

    private class MoveUp extends  AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {

            if ( inGame ){
                if( direction != Move.DOWN && moveMade ){
                    direction = Move.UP;
                    moveMade = false;

                }

            }

        }
    }

    private class MoveDown extends  AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {

            if ( inGame ){
                if ( direction != Move.UP  && moveMade ){
                    direction = Move.DOWN;
                    moveMade = false;

                }

            }

        }
    }

    private class MoveLeft extends  AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {

            if ( inGame ){
                if ( direction != Move.RIGHT && moveMade  ){
                    direction = Move.LEFT;
                    moveMade = false;

                }

            }

        }
    }

    private class MoveRight extends  AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {

            if ( inGame ){
                if ( direction != Move.LEFT && moveMade ){
                    direction = Move.RIGHT;
                    moveMade = false;

                }

            }

        }
    }

    @Override
    public void paintComponent ( Graphics g ){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        if ( !inGame ){
            showIntroGame(g2d);//draws the animation for the intro
        }else{

            if ( !gameOver ){

                drawFullGame(g2d);//draws the game while playing

                if ( pause ){
                    //if the user press the space bar the game is paused
                    timerGame.stop();
                    drawPauseLogo( g2d );
                }

            }else{

                timerGame.stop();
                drawGameOverLogo(g2d);

            }

        }


    }

    private void showIntroGame( Graphics2D g2d ){

        //Draw big letters for the logo
        g2d.setPaint( Color.WHITE );
        g2d.setFont( new Font(Font.SERIF, Font.ITALIC+Font.BOLD, 170) );
        g2d.drawString("Snake", WINDOW_WIDTH /2 -210, 175);
        g2d.setPaint( new Color(174, 49, 43) );
        g2d.setFont( new Font(Font.SERIF, Font.ITALIC+Font.BOLD, 160) );
        g2d.drawString("Snake", WINDOW_WIDTH /2 -200, 165);

        drawFrameForBigLettersInIntro(g2d);

        //Piece of food
        snakeFood.setPosition(foodX, foodY);
        snakeFood.draw( g2d );

        //Snake head and body
        snakeHead.setPosition(headX, headY);
        drawSnakeBody( g2d );
        snakeHead.draw(g2d);


        if ( flickeringLetters ){
            g2d.setPaint( Color.WHITE );
        }else{
            g2d.setPaint( Color.LIGHT_GRAY );
        }

        g2d.setFont( new Font(Font.SERIF,Font.ITALIC+Font.BOLD , 45) );
        g2d.drawString("Press <enter> to play", 100, WINDOW_HEIGHT/2+80);

    }

    private void drawFrameForBigLettersInIntro(Graphics2D g2d ){

        int x = 40;
        int y = 20;

        int size= 67 ;

        for ( int i = 1; i <= size; i++ ){

            int green = 255 / size * i;
            g2d.setPaint( new Color (200,green,0) );


            if ( i > 25 && i < 35 ){

                y += DOT_SIZE;
                g2d.fillOval(x, y, DOT_SIZE,DOT_SIZE);

            }else if ( i >= 35 && i < 59 ){

                x -= DOT_SIZE;
                g2d.fillOval(x, y, DOT_SIZE,DOT_SIZE);

            }else if (  i > 36 ){

                y -= DOT_SIZE;
                g2d.fillOval(x, y, DOT_SIZE,DOT_SIZE);

            }else{

                x += DOT_SIZE;
                g2d.fillOval(x, y, DOT_SIZE,DOT_SIZE);

            }
        }
    }

    private void restarSnakeIntro (){

        //move the snake's head to the center
        headY  = WINDOW_HEIGHT / 2;
        headX  = WINDOW_WIDTH / 2;

        sizeSnake = 3;//initial size

        Color color = new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255));
        for ( int j = 0;j <= sizeSnake; j++){
            snakeBody[j] = new Snake(headX, headY,  DOT_SIZE);
            snakeBody[j].setCharacteristics(shapeOfSnake, color, filledSnake);

        }
        snakeHead.setColor( color.brighter().brighter().brighter() );

        //intial direction for the intro
        switch ( random.nextInt(2 )){
            case 0 :
                direction = Move.RIGHT;
                break;
            case 1 :
                direction = Move.LEFT;
                break;
        }
        relocateFood();//place the food

    }

    public void restarGame (){


        for ( int i = 0; i <= sizeSnake; i++ ){ snakeBody[i] = null; }

        //move the snake's head to the center
        headY  = WINDOW_HEIGHT / 2;
        headX  = WINDOW_WIDTH / 2;

        sizeSnake = 3;//initial size
        score = sizeSnake * 10;//initial score

        Color color = new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255));
        for ( int j = 0;j <= sizeSnake; j++){
            snakeBody[j] = new Snake(headX, headY,  DOT_SIZE);
            snakeBody[j].setCharacteristics(shapeOfSnake, color, filledSnake);

        }

        snakeHead.setColor( color.brighter().brighter().brighter() );

        //initial direction
        switch (random.nextInt(4)){
            case 0:
                direction = Move.RIGHT;
                break;
            case 1:
                direction = Move.UP;
                break;
            case 2:
                direction = Move.DOWN;
                break;
            case 3:
                direction = Move.LEFT;
                break;
        }

        relocateFood();//place the food

        gameOver = false;
        inGame = true;

        if ( !timerGame.isRunning() ){
            timerGame.start();
        }

    }

    private void drawFullGame ( Graphics2D  g2d ){

        //Piece of food
        snakeFood.setPosition(foodX, foodY);
        snakeFood.draw( g2d );

        //Snake head and body
        snakeHead.setPosition(headX, headY);
        drawSnakeBody( g2d );
        snakeHead.draw(g2d);

        //Score
        showScore(g2d);
    }

    private void showScore ( Graphics2D  g2d ){


        int x = WINDOW_WIDTH - 80;
        int y = 10;
        int with = 70;
        int height  = 40;
        int arcWidth = 5;
        int arcHeight = 5;

        Font scoreFontNumbers = new Font(Font.MONOSPACED, Font.PLAIN, 20);
        Font scoreFontLetters = new Font(Font.MONOSPACED, Font.PLAIN, 13);

        g2d.setPaint( Color.ORANGE );
        g2d.setStroke( new BasicStroke(1.0f) );

        boolean foodUnderScore;

        //checks if the food is placed in the score's area
        if ( ( foodX >= x &&  foodX <= x + with ) && ( (foodY >= y  && foodY <=  y + height) || (foodY + DOT_SIZE >= y  && foodY + DOT_SIZE <=  y + height) ) ){
            foodUnderScore = true;
        }else
            foodUnderScore = (foodX + DOT_SIZE >= x && foodX + DOT_SIZE <= x + with) && (foodY + DOT_SIZE >= y && foodY + DOT_SIZE <= y + height);

        //move the score if the food is placed randomly in its place
        if ( !foodUnderScore ){

            g2d.setFont( scoreFontNumbers );

            if ( score < 99 ){
                g2d.drawString("000"+String.valueOf(score), WINDOW_WIDTH - 75 ,30);
            }else if ( score < 999 ){
                g2d.drawString("00"+String.valueOf(score), WINDOW_WIDTH - 75 ,30);
            }else if ( score < 9999 ){
                g2d.drawString("0"+String.valueOf(score), WINDOW_WIDTH - 75 ,30);
            }

            g2d.setFont( scoreFontLetters );
            g2d.drawString("SCORE", WINDOW_WIDTH - 55, 45 );

            g2d.drawRoundRect(x ,y, with,height, arcWidth, arcHeight);

        }else{

            g2d.setFont( scoreFontNumbers );

            if ( score < 99 ){
                g2d.drawString("000"+String.valueOf(score),  15 ,30);
            }else if ( score < 999){
                g2d.drawString("00"+String.valueOf(score),  15 ,30);
            }else if ( score < 9999 ){
                g2d.drawString("0"+String.valueOf(score), 15 ,30);

            }

            g2d.setFont( scoreFontLetters );
            g2d.drawString("SCORE", 35, 45 );

            g2d.drawRoundRect(10,10, with,height, arcWidth, arcHeight);

        }


    }

    private void drawSnakeBody( Graphics2D g2d ){

        snakeBody[0].copyDataSnake(snakeHead);

        for ( int i = sizeSnake; i > 0 ; i-- ){

            snakeBody[i].copyDataSnake(snakeBody[i-1]);
            snakeBody[i].draw(g2d);

        }

    }

    private void moveSnake (){

        switch (direction){
            case UP:

                if( headY  == 0 ){
                    headY = WINDOW_HEIGHT - DOT_SIZE;
                }else{
                    headY -= DOT_SIZE;
                }

                moveMade = true;

                break;
            case DOWN:

                if ( headY == WINDOW_HEIGHT - DOT_SIZE){
                    headY = 0;
                }else{
                    headY +=  DOT_SIZE;
                }

                moveMade = true;

                break;
            case LEFT:

                if ( headX == 0 ){
                    headX = WINDOW_WIDTH - DOT_SIZE;
                }else{
                    headX -= DOT_SIZE;
                }

                moveMade = true;

                break;
            case RIGHT:

                if ( headX == WINDOW_WIDTH - DOT_SIZE ){
                    headX = 0;
                }else{
                    headX += DOT_SIZE ;
                }

                moveMade = true;

                break;

        }


    }

    private boolean checkIfSnakeEats(){

        if ( snakeHead.getX() == foodX && snakeHead.getY() == foodY ){

            sizeSnake++;
            snakeBody[sizeSnake] = new Snake(snakeBody[sizeSnake-1]);//copy the characteristics of the body
            score = sizeSnake * 10;

            return true;
        }else{
            return false;
        }


   }

    private boolean checkCollision(){

       for ( int i = 4; i <= sizeSnake; i++ ){

           if ( snakeHead.getX() == snakeBody[i].getX() && snakeHead.getY() == snakeBody[i].getY() ){
               return true;
           }
       }

       return false;
   }

    private void relocateFood(){

        boolean outOfSnake;

        int randomX;
        int randomY;

        do{
            outOfSnake = true;
            randomX = DOT_SIZE+DOT_SIZE*random.nextInt(WINDOW_WIDTH/DOT_SIZE);
            randomY = DOT_SIZE+DOT_SIZE*random.nextInt(WINDOW_HEIGHT/DOT_SIZE);

            if ( randomX == WINDOW_WIDTH  ){
                randomX = 0;
            }
            if ( randomY == WINDOW_HEIGHT  ){
                randomY = 0;
            }

            for ( int i = 0; i <= sizeSnake; i++ ){
                if ( snakeBody[i].getX() == randomX && snakeBody[i].getY() == randomY ){
                    outOfSnake = false;
                    break;
                }
            }


        }while( !outOfSnake );


        foodX = randomX;


        if ( !inGame ){
           foodY = headY;//place the food in the snake's Y direction (  only for intro's game )
        }else{
           foodY = randomY;
        }

        snakeFood.setColor( new Color( random.nextInt(255),random.nextInt(255),random.nextInt(255)).brighter());

   }

    private void drawGameOverLogo ( Graphics2D g2d ){


        for ( int i = 0; i <= sizeSnake; i++ ){
            snakeBody[i].setColor(snakeBody[0].getColor().darker());
            snakeBody[i].draw(g2d);
        }

        snakeHead.setColor(snakeHead.getColor().darker());
        snakeHead.draw(g2d);


        //draw the outer circles that mark the percentage of the game reach by the user
        double percentageReached =  ( sizeSnake * 100) / snakeBody.length  ;
        double arcExtent = percentageReached *  360 / 100;

        g2d.setStroke( new BasicStroke(10.0f) );
        g2d.setPaint( new Color(140, 102, 94) );
        g2d.draw( new Arc2D.Double(WINDOW_WIDTH / 2 - 250 , 60, 500,480, 90 , -360, Arc2D.OPEN ));
        g2d.setPaint( new Color(174, 49, 43) );
        g2d.fill( new Arc2D.Double(WINDOW_WIDTH / 2 - 250 , 60, 500,480, 90 , -arcExtent, Arc2D.PIE ));

        //draw the letters of the logo
        g2d.setPaint( new Color ( 211,140, 50).brighter() );
        g2d.setFont( new Font(Font.SERIF, Font.ITALIC+Font.BOLD, 80) );
        g2d.drawString("Game Over", WINDOW_WIDTH / 2 - 190 , WINDOW_HEIGHT /2 - 100);
        g2d.setFont( new Font(Font.SERIF, Font.ITALIC+Font.BOLD, 50) );

        int xLocationScore = 0;
        if ( score < 99 ){
            xLocationScore = 130;
        }else if ( score < 999 ){
            xLocationScore = 140;
        }else if ( score < 9999 ){
            xLocationScore = 150;
        }
        g2d.drawString("!Score = "+score+"!", WINDOW_WIDTH / 2 - xLocationScore , WINDOW_HEIGHT /2+50 -100);

        g2d.setPaint( Color.WHITE.darker() );
        g2d.setFont( new Font(Font.SERIF, Font.BOLD+Font.ITALIC, 90) );
        g2d.drawString((int)percentageReached+"%", WINDOW_WIDTH/2-85, WINDOW_HEIGHT/2+50);
        g2d.setFont( new Font(Font.SERIF, Font.BOLD+Font.ITALIC, 40) );
        g2d.drawString("Completed", WINDOW_WIDTH/2-100, WINDOW_HEIGHT/2+120);

        g2d.setFont( new Font(Font.SERIF, Font.ITALIC+ Font.BOLD, 25) );
        g2d.setPaint( Color.WHITE );
        g2d.drawString("Press <enter> to restart", WINDOW_WIDTH/2-130, WINDOW_HEIGHT/2+160);

    }

    private void drawPauseLogo ( Graphics2D g2d ){

        //draw background rect
        g2d.setPaint( new Color(48, 50, 52) );
        g2d.fill3DRect(60 , WINDOW_HEIGHT /2 - 80, 500 , 120 ,true);

        g2d.setPaint( new Color ( 211,140, 50) );
        g2d.setFont( new Font(Font.SERIF, Font.ITALIC+Font.BOLD, 80) );
        g2d.drawString("Game Paused", 80 , WINDOW_HEIGHT /2);
        g2d.setPaint( Color.WHITE );
        g2d.setFont( new Font(Font.SERIF,Font.ITALIC+Font.BOLD , 20) );
        g2d.drawString("Press <space> for resume",WINDOW_WIDTH / 2 - 90 , WINDOW_HEIGHT /2 + 25 );

    }

    public void setShapeOfSnake( int shapeOfSnake ){
        this.shapeOfSnake = shapeOfSnake;
        snakeHead.setShape(shapeOfSnake);
        snakeFood.setShape(shapeOfSnake);
        for ( int i = 0; i <= sizeSnake; i++){  snakeBody[i].setShape(shapeOfSnake);   }

    }

    public void setSnakeFilled ( boolean filledSnake ){

        this.filledSnake = filledSnake;
        snakeHead.setFilled(this.filledSnake);
        snakeFood.setFilled(filledSnake);
        for ( int i = 0; i <= sizeSnake; i++){  snakeBody[i].setFilled(filledSnake);   }

    }

    public void changeSnakeColor (){
        Color color = new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255));
        for ( int j = 0;j <= sizeSnake; j++){ snakeBody[j].setColor(color); }
        snakeHead.setColor( color.brighter().brighter().brighter() );
    }

    public Timer getTimerGame (){
        return timerGame;
    }

    public void setSupportsSound(boolean supportsSound) {
        this.supportsSound = supportsSound;
    }

    public boolean isSoundSupported() {
        return supportsSound;
    }

    private boolean getSoundEffects(){

        eatSoundClip = getClip("eatSound.wav", 1.0f);
        if ( eatSoundClip  == null ){return false;}

        gameOverSoundClip = getClip("gameOverSound.wav", 1.0f);
        if ( gameOverSoundClip == null ){
            eatSoundClip = null;
            return false;
        }

        return true;
    }

    private Clip getClip ( String fileName , float gain ){

        URL url = getClass().getResource( fileName );
        if ( url == null ){
            return null;
        }

        Clip clip = null;

        try {
            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(url));
            clip.setFramePosition(0);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(SnakeGame.this, e,"LineUnavailableException", JOptionPane.ERROR_MESSAGE );
            supportsSound = false;
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(SnakeGame.this, e,"UnsupportedAudioFileException", JOptionPane.ERROR_MESSAGE );
            supportsSound = false;
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(SnakeGame.this, e,"IOException", JOptionPane.ERROR_MESSAGE );
            supportsSound = false;
        }

        FloatControl masterGain = (FloatControl) (clip != null ? clip.getControl(FloatControl.Type.MASTER_GAIN) : null);
        masterGain.setValue(gain);

        return clip;
    }




}//end of class
