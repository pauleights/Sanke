import java.awt.*;

public class Snake {

    //Constants : Type of snakes for the game
    public static final int SQUARE_SHAPE = 0;
    public static final int ROUND_SHAPE = 1;

    private int x;
    private int y;
    private int shape;
    private int dotSize;
    private Color color;
    private boolean filled;



    public Snake ( Snake snake ){
        x = snake.getX();
        y = snake.getY();
        dotSize = snake.getDotSize();
        shape = snake.getShape();
        color = snake.getColor();
        filled = snake.getFilled();
    }

    public Snake ( int  x , int y ,int dotSize ){
        this.x = x;
        this.y = y;
        this.dotSize = dotSize;
    }


    public Snake (int dotSize ){
        this.dotSize = dotSize;
    }


    public void setCharacteristics(int shape, Color color, boolean fill){
        this.shape = shape;
        this.color = color;
        filled = fill;
    }

    public void setPosition(int  x , int y ){
        this.x = x;
        this.y = y;
    }

    public int getX (){
        return x;
    }

    public int getY (){
        return y;
    }

    public void copyDataSnake ( Snake snake ){
        x = snake.getX();
        y = snake.getY();
    }

    public void setColor ( Color color ){
        this.color = color;
    }

    public Color getColor (){
        return color;
    }

    public void draw ( Graphics2D g2d ){
        g2d.setPaint( getColor() );

        switch ( shape ){
            case ROUND_SHAPE:

                if ( filled ){
                    g2d.fillOval( x, y, dotSize, dotSize );
                }else{
                    g2d.setStroke( new BasicStroke( 3.0f ) );
                    g2d.drawOval( x, y, dotSize, dotSize );
                }

                break;
            case SQUARE_SHAPE :

                if ( filled ){
                    g2d.fillRect( x, y, dotSize, dotSize );
                }else{
                    g2d.setStroke( new BasicStroke( 3.0f ) );
                    g2d.drawRect( x, y, dotSize, dotSize );
                }

                break;
        }

    }

    public void setFilled ( boolean filled ){
        this.filled = filled;
    }

    private boolean getFilled() {
        return filled;
    }

    public void setShape ( int shape ){
        this.shape = shape;
    }

    private int getShape() {
        return shape;
    }

    private int getDotSize() {
        return dotSize;
    }


}//end of class
