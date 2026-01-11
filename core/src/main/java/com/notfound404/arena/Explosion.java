package com.notfound404.arena;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

//This class implements explosion effects in the arena
public class Explosion {
    private static final int PARTICLE_COUNT = 20;
    private Particle[] particles;
    private final int centerX;
    private final int centerY;

    public Explosion(int centerX, int centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
        particles = new Particle[PARTICLE_COUNT];
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            particles[i] = new Particle(centerX, centerY);
        }
    }

    public boolean update(float deltaTime) {
        boolean isDone = true;
        for (Particle p : particles) {
            if(p.update(deltaTime)) {
                isDone = false;
            }
        }
        return isDone;
    }

    class Particle{
        private float x, y;
        Color color;
        float lifeTime;
        float maxLifeTime;
        float velX, velY;
        float accumulatorX, accumulatorY;

        //Hit the Trail
        public Particle(int startX, int startY) {
            this.x = 0;
            this.y = 0;
            this.color = new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1);
            this.maxLifeTime = MathUtils.random(0.8f, 1.5f);
            this.lifeTime = maxLifeTime;
            float angle = MathUtils.random(0, 2 * MathUtils.PI);
            float speed = MathUtils.random(20, 50);
            this.velX = MathUtils.cos(angle) * speed;
            this.velY = MathUtils.sin(angle) * speed;
            this.accumulatorX = 0;
            this.accumulatorY = 0;
        }

        public boolean update(float deltaTime) {
            if (lifeTime <= 0) return false;
            lifeTime -= deltaTime;          

            accumulatorX += velX * deltaTime;
            accumulatorY += velY * deltaTime;

            x += accumulatorX;
            y += accumulatorY;

            accumulatorX -= accumulatorX;
            accumulatorY -= accumulatorY;

            return lifeTime >0;
        }

        public float getX(){return x;}
        public float getY(){return y;}
    }

    public void draw(ShapeRenderer painter){
        int baseX = centerX * 9;
        int baseY = centerY * 9;
        for(Particle particle:particles){
            painter.setColor(particle.color);
            painter.rect(baseX + particle.getX(), baseY + particle.getY(), 2, 2);
        }
    }
}
