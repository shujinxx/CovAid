package com.ganshujin.androidrealtimelocation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class GameView extends View {

    private Bitmap fish[] = new Bitmap[2];
    private int fishX = 10;
    private int fishY;
    private int fishSpeed;

    private int canvasWidth, canvasHeight;

    private int yellowX, yellowY, yellowSpeed = 16;
    private Paint yellowPaint = new Paint();

    private int greenX, greenY, greenSpeed = 20;
    private Paint greenPaint = new Paint();

    private int redX, redY, redSpeed = 50;
    private Paint redPaint = new Paint();

    private int score, lifeCounterOfFish;

    private Bitmap backgroundimage;
    private Paint scorepaint = new Paint();
    private Bitmap life[] = new Bitmap[2];

    private Boolean touch = false;


    public GameView(Context context) {
        super(context);

        fish[0] = BitmapFactory.decodeResource(getResources(), R.drawable.paperwau2cc_64x64);
        fish[1] = BitmapFactory.decodeResource(getResources(), R.drawable.paperwau1cc_64x64);

        backgroundimage = BitmapFactory.decodeResource(getResources(), R.drawable.backgrounddotted);

        yellowPaint.setColor(Color.YELLOW);
        yellowPaint.setAntiAlias(false);

        greenPaint.setColor(Color.GREEN);
        greenPaint.setAntiAlias(false);

        redPaint.setColor(Color.RED);
        redPaint.setAntiAlias(false);

        scorepaint.setColor(Color.BLACK);
        scorepaint.setTextSize(70);
        scorepaint.setTypeface(Typeface.DEFAULT_BOLD);
        scorepaint.setAntiAlias(true);

        life[0] = BitmapFactory.decodeResource(getResources(), R.drawable.hearts);
        life[1] = BitmapFactory.decodeResource(getResources(), R.drawable.loselife);

        fishY = 550;
        score = 0;
        lifeCounterOfFish = 3;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvasWidth = getWidth();
        canvasHeight = getHeight();


        canvas.drawBitmap(backgroundimage, 0,0,null);

        int minFishY = fish[0].getHeight();
        int maxFishY = canvasHeight - fish[0].getHeight() *3;
        fishY = fishY + fishSpeed;

        if (fishY < minFishY)
        {
            fishY = minFishY;
        }
        if (fishY > maxFishY)
        {
            fishY = maxFishY;
        }
        fishSpeed = fishSpeed +2;

        if (touch)
        {
            canvas.drawBitmap(fish[1], fishX, fishY, null);
            touch = false;
        }
        else
        {
            canvas.drawBitmap(fish[0], fishX, fishY, null);
        }

        yellowX = yellowX - yellowSpeed;

        if (hitBallChecker(yellowX, yellowY))
        {
            score = score + 10;
            yellowX = - 100;
        }

        if (yellowX<0)
        {
            yellowX = canvasWidth + 21;
            yellowY = (int) Math.floor(Math.random() * (maxFishY - minFishY) + minFishY);
        }
        canvas.drawCircle(yellowX,yellowY,30,yellowPaint);


        greenX = greenX - greenSpeed;

        if (hitBallChecker(greenX, greenY))
        {
            score = score + 5;
            greenX = - 100;
        }

        if (greenX<0)
        {
            greenX = canvasWidth + 21;
            greenY = (int) Math.floor(Math.random() * (maxFishY - minFishY) + minFishY);
        }
        canvas.drawCircle(greenX,greenY,60,greenPaint);

        redX = redX - redSpeed;

        if (hitBallChecker(redX, redY))
        {
            redX = -100;
            lifeCounterOfFish--;

            if (lifeCounterOfFish == 0)
            {
                Toast.makeText(getContext(), "Game Over", Toast.LENGTH_SHORT).show();

                Intent gameOverIntent = new Intent(getContext(), GameOver.class);
                gameOverIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                gameOverIntent.putExtra("score", score);
                getContext().startActivity(gameOverIntent);
            }
        }

        if (redX<0)
        {
            redX = canvasWidth + 100;
            redY = (int) Math.floor(Math.random() * (maxFishY - minFishY)) ;
        }
        canvas.drawCircle(redX,redY,100,redPaint);


        canvas.drawText("Score : " + score, 20,80,scorepaint);

        for (int i=0; i<3; i++)
        {
            int x = (int) (600 + life[0].getWidth() * 1.5 * i);
            int y = 30;

            if (i < lifeCounterOfFish)
            {
                canvas.drawBitmap(life[0], x, y, null);
            }
            else
            {
                canvas.drawBitmap(life[1], x, y, null);
            }
        }

    }

    public boolean hitBallChecker(int x, int y)
    {
        if (fishX < x && x < (fishX + fish[0].getWidth()) && fishY < y && y<(fishY+fish[0].getHeight()))
        {
            return true;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            touch = true;
            fishSpeed = -26;
        }
        return true;
    }
}
