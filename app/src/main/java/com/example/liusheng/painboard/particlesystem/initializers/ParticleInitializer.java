package com.example.liusheng.painboard.particlesystem.initializers;


import com.example.liusheng.painboard.particlesystem.Particle;

import java.util.Random;

public interface ParticleInitializer {

	void initParticle(Particle p, Random r);

}
