package com.example.a79373.grifchat;

import android.support.v7.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Random;

public class Game_activity extends AppCompatActivity implements View.OnTouchListener {
    public static int maxX = 20; // размер по горизонтали
    public static int maxY = 28; // размер по вертикали
    public static float unitW = 0; // пикселей в юните по горизонтали
    public static float unitH = 0; // пикселей в юните по вертикали
    public static boolean isLeftPressed = false; // нажата левая кнопка
    public static boolean isRightPressed = false; // нажата правая кнопка
    public Thread gameThread = null;
    public boolean gameRunning = true;
    private Paint mScorePaint;
    public int mScore=0;
    public ArrayList<Asteroid> asteroids = new ArrayList<>();
    public ArrayList<Riba> ribas = new ArrayList<>();
    public int indexasteroid=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_activity);
        GameView gameView = new GameView(this); // создаём gameView

        LinearLayout gameLayout = (LinearLayout) findViewById(R.id.gameLayoutt); // находим gameLayout
        gameLayout.addView(gameView); // и добавляем в него gameView

        Button leftButton = (Button) findViewById(R.id.leftButtonn); // находим кнопки
        Button rightButton = (Button) findViewById(R.id.rightButtonn);

        // стили для вывода счета
        mScorePaint = new Paint();
        mScorePaint.setTextSize(20);
        mScorePaint.setStrokeWidth(1);
        mScorePaint.setTextAlign(Paint.Align.CENTER);


        startService(new Intent  (this, MyService.class));
        leftButton.setOnTouchListener(this); // и добавляем этот класс как слушателя (при нажатии сработает onTouch)
        rightButton.setOnTouchListener(this);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }
    @Override
    protected void onPause (){
        super.onPause();
        stopMusic();

    }
    @Override
    protected void onResume (){
        super.onResume();

    }


    public boolean onTouch(View button, MotionEvent motion) {
        switch(button.getId()) { // определяем какая кнопка
            case R.id.leftButtonn:
                switch (motion.getAction()) { // определяем нажата или отпущена
                    case MotionEvent.ACTION_DOWN:
                        isLeftPressed = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        isLeftPressed = false;
                        break;
                }
                break;
            case R.id.rightButtonn:
                switch (motion.getAction()) { // определяем нажата или отпущена
                    case MotionEvent.ACTION_DOWN:
                        isRightPressed = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        isRightPressed = false;
                        break;
                }
                break;
        }
        return true;
    }


    public class GameView extends SurfaceView implements Runnable{
        private final int ASTEROID_INTERVAL = 35; // время через которое появляются астероиды (в итерациях)
        private int currentTime = 0;
        private int currentTimeriba = 0;
        private boolean firstTime = true;
        private Ship ship;

        private Paint paint;
        private Canvas canvas;
        private SurfaceHolder surfaceHolder;


        public GameView(Context context) {
            super(context);
            //инициализируем обьекты для рисования
            surfaceHolder = getHolder();
            paint = new Paint();

            // инициализируем поток
            gameThread = new Thread(this);
            gameThread.start();
        }
        @Override
        public void run() {

            while (gameRunning) {

                update();
                draw();
                checkCollisionriba();
                checkCollision();
                checkIfNewAsteroid();
                checkIfNewriba();
                control();


            }

            isLeftPressed=false;
            isRightPressed=false;
            stopMusic();
            Intent intent = new Intent(Game_activity.this, StartGameActivity.class);
            startActivity(intent);
            finish();

        }
        private void update() {


            if(!firstTime) {
                ship.update();
                for (Asteroid asteroid : asteroids) {
                    asteroid.update();

                }
                for (Riba riba : ribas) {
                    riba.update();

                }
            }
        }

        private void draw() {

            if (surfaceHolder.getSurface().isValid()) {  //проверяем валидный ли surface

                if(firstTime){ // инициализация при первом запуске
                    firstTime = false;
                    unitW = surfaceHolder.getSurfaceFrame().width()/maxX; // вычисляем число пикселей в юните
                    unitH = surfaceHolder.getSurfaceFrame().height()/maxY;

                    ship = new Ship(getContext()); // добавляем корабль
                }

                canvas = surfaceHolder.lockCanvas(); // закрываем canvas
                canvas.drawColor(Color.BLACK); // заполняем фон чёрным
                //canvas.drawBitmap();

                ship.drow(paint, canvas); // рисуем корабль
                for(Asteroid asteroid: asteroids){ // рисуем астероиды
                    asteroid.drow(paint, canvas);
                }
                for(Riba riba: ribas){ // рисуем астероиды
                    riba.drow(paint, canvas);
                }
                mScorePaint.setTextSize(100);
                mScorePaint.setColor(Color.RED);
                canvas.drawText(String.valueOf(mScore),970, 100, mScorePaint);

                surfaceHolder.unlockCanvasAndPost(canvas); // открываем canvas
            }
        }

        private void control() { // пауза на 17 миллисекунд
            try {
                gameThread.sleep(17);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        private void checkCollision(){ // перебираем все астероиды и проверяем не касается ли один из них корабля
            for (Asteroid asteroid : asteroids) {
                if(asteroid.isCollision(ship.x, ship.y, ship.size)){
                    // игрок проиграл
                    gameRunning = false;

                    // останавливаем игру
                    // TODO добавить анимацию взрыва
                }
            }

        }
        private void checkCollisionriba() {
            for (Riba riba : ribas) {
                if (riba.isCollisionriba(ship.x, ship.y, ship.size)) {
                    ribas.remove(0);
                    mScore = mScore + 10;

                    // останавливаем игру
                    // TODO добавить анимацию взрыва
                }
            }
        }
        private void checkIfNewAsteroid(){ // каждые 50 итераций добавляем новый астероид
            Random randomez = new Random();
            if(currentTime >= randomez.nextInt(20)+ASTEROID_INTERVAL){
                Asteroid asteroid = new Asteroid(getContext());
                asteroids.add(asteroid);
                currentTime = 0;
            }else{
                currentTime ++;
            }
        }
        private void checkIfNewriba(){
            Random randome = new Random();// каждые 500 итераций добавляем новый астероид
            if(currentTimeriba >= randome.nextInt(200)+520 ){
                Riba riba = new Riba(getContext());
                ribas.add(riba);
                currentTimeriba = 0;
            }else{
                currentTimeriba ++;
            }
        }
    }

    public class SpaceBody{
        protected float x; // координаты
        protected float y;

        protected float size; // размер
        protected float speed; // скорость
        protected int bitmapId; // id картинки
        protected Bitmap bitmap; // картинка
        void init(Context context) { // сжимаем картинку до нужных размеров
            Bitmap cBitmap = BitmapFactory.decodeResource(context.getResources(), bitmapId);
            bitmap = Bitmap.createScaledBitmap(
                    cBitmap, (int)(size * Game_activity.unitW), (int)(size * Game_activity.unitH), false);
            cBitmap.recycle();
        }

        void update(){ // тут будут вычисляться новые координаты
        }

        void drow(Paint paint, Canvas canvas){ // рисуем картинку
            canvas.drawBitmap(bitmap, x* Game_activity.unitW, y* Game_activity.unitH, paint);
        }
    }
    public class Ship extends SpaceBody{
        public Ship(Context context) {
            bitmapId = R.drawable.grifon; // определяем начальные параметры
            size = 5;
            x=7;
            y= Game_activity.maxY - size - 1;
            speed = (float) 0.27;

            init(context); // инициализируем корабль
        }
        @Override
        public void update() { // перемещаем корабль в зависимости от нажатой кнопки
            if(Game_activity.isLeftPressed && x >= 0){
                x -= 1.3*speed;
            }
            if(Game_activity.isRightPressed && x <= Game_activity.maxX - 5){
                x += 1.3*speed;

            }
        }
    }
    public class Asteroid extends SpaceBody{
        boolean sc=false;
        private int radius = 2; // радиус
        private float minSpeed = (float) 0.4; // минимальная скорость
        private float maxSpeed = (float) 0.5; // максимальная скорость
        public Asteroid(Context context) {
            Random random = new Random();

            bitmapId = R.drawable.asteroid;
            y=0;
            x = random.nextInt(Game_activity.maxX-4) + 2;
            size = radius*2;
            speed = minSpeed + (maxSpeed - minSpeed) * random.nextFloat();

            init(context);
        }
        @Override
        public void update() {

            y += speed;
            if(y>28 && !sc )
            {

                incScore();
                sc=true;

            }
        }
        public boolean isCollision(float shipX, float shipY, float shipSize) {

            return !(((x+size)-1 < shipX)||(x+1 > (shipX+shipSize))||((y+size)-1 < shipY)||(y+1 > (shipY+shipSize)));
        }
    }
    public class Riba extends SpaceBody{

        private int radiusriba = 2; // радиус
        private float minSpeedriba = (float) 0.4; // минимальная скорость
        private float maxSpeedriba = (float) 0.5; // максимальная скорость
        public Riba(Context context) {
            Random random = new Random();

            bitmapId = R.drawable.normriba;
            y=0;
            x = random.nextInt(Game_activity.maxX-4) + 2;
            size = radiusriba*2;
            speed = minSpeedriba + (maxSpeedriba - minSpeedriba) * random.nextFloat();

            init(context);
        }
        @Override
        public void update() {
            y += speed;
            if(y>28)
            {
                ribas.remove(0);
            }
        }
        public boolean isCollisionriba(float shipX, float shipY, float shipSize) {

            return !(((x+size)-1 < shipX)||(x+1 > (shipX+shipSize))||((y+size)-1 < shipY)||(y+1 > (shipY+shipSize)));
        }
    }

    public void stopMusic (){
        stopService(new Intent(this, MyService.class));
    }
    public void onBackPressed(){
        gameRunning=false;
        isLeftPressed=false;
        isRightPressed=false;
        stopMusic();
        Intent Murrintent = new Intent(Game_activity.this, StartGameActivity.class);
        startActivity(Murrintent);
        finish();
    }
    public void incScore()
    {
        mScore++;
    }
}