/*
 Attributes.java
 Copyright (C) 2003, 2006 Gerardo Horvilleur Martinez

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

package org.jrman.attributes;

import java.util.Iterator;
import java.util.Set;

import javax.vecmath.Color3f;

import org.jrman.geom.Bounds3f;
import org.jrman.geom.Transform;
import org.jrman.shaders.DisplacementShader;
import org.jrman.shaders.LightShader;
import org.jrman.shaders.SurfaceShader;
import org.jrman.shaders.VolumeShader;


public class Attributes {

    protected Color3f color;

    protected Color3f opacity;

    protected TextureCoordinates textureCoordinates;

    protected Set<LightShader> lightSources;
    
    protected LightShader[] lightSourcesArray;

    protected SurfaceShader surface;

    protected DisplacementShader displacement;

    protected VolumeShader atmosphere;

    protected VolumeShader interior;

    protected VolumeShader exterior;

    protected float shadingRate;

    protected ShadingInterpolation shadingInterpolation;

    protected boolean matte;

    protected Bounds3f bound;

    protected Bounds3f detail;

    protected DetailRange detailRange;

    protected GeometricApproximation geometricApproximation;

    protected Orientation orientation;

    protected int sides;

    protected Transform transform;

    protected float displacementBound;

    protected String displacementBoundCoordinateSystem;

    protected String objectIdentifier;

    protected TrimCurveSense trimCurveSense;
    
    protected Basis uBasis;
    
    protected int uStep;
    
    protected Basis vBasis;
    
    protected int vStep;
    
    protected Space space;

    protected boolean trueDisplacement;
    
   protected Attributes() {
    }

    public Attributes(Attributes other) {
        color = other.getColor();
        opacity = other.getOpacity();
        textureCoordinates = other.getTextureCoordinates();
        lightSources = other.getLightSources();
        surface = other.getSurface();
        displacement = other.getDisplacement();
        atmosphere = other.getAtmosphere();
        interior = other.getInterior();
        exterior = other.getExterior();
        shadingRate = other.getShadingRate();
        shadingInterpolation = other.getShadingInterpolation();
        matte = other.isMatte();
        bound = other.getBound();
        detail = other.getDetail();
        detailRange = other.getDetailRange();
        geometricApproximation = other.getGeometricApproximation();
        orientation = other.getOrientation();
        sides = other.getSides();
        transform = other.getTransform();
        displacementBound = other.getDisplacementBound();
        displacementBoundCoordinateSystem = other.getDisplacementBoundCoordinateSystem();
        objectIdentifier = other.getObjectIdentifier();
        trimCurveSense = other.getTrimCurveSense();
        uBasis = other.getUBasis();
        uStep = other.getUStep();
        vBasis = other.getVBasis();
        vStep = other.getVStep();
        space = other.getSpace();
        trueDisplacement = other.getTrueDisplacement();
    }

    public Color3f getColor() {
        return new Color3f(color);
    }

    public Color3f getOpacity() {
        return new Color3f(opacity);
    }

    public TextureCoordinates getTextureCoordinates() {
        return textureCoordinates;
    }

    public Set<LightShader> getLightSources() {
        // return Collections.unmodifiableSet(lightSources);
        return lightSources;
    }
    
    public LightShader[] getLightSourcesArray() {
        if (lightSourcesArray == null) {
            lightSourcesArray = new LightShader[lightSources.size()];
            int p = 0;
            for (Iterator iter = lightSources.iterator(); iter.hasNext(); )
                lightSourcesArray[p++] = (LightShader) iter.next();
        }
        return lightSourcesArray;
    }

    public SurfaceShader getSurface() {
        return surface;
    }

    public DisplacementShader getDisplacement() {
        return displacement;
    }

    public VolumeShader getAtmosphere() {
        return atmosphere;
    }

    public VolumeShader getInterior() {
        return interior;
    }

    public VolumeShader getExterior() {
        return exterior;
    }

    public float getShadingRate() {
        return shadingRate;
    }

    public ShadingInterpolation getShadingInterpolation() {
        return shadingInterpolation;
    }

    public boolean isMatte() {
        return matte;
    }

    public Bounds3f getBound() {
        return bound;
    }

    public Bounds3f getDetail() {
        return detail;
    }

    public DetailRange getDetailRange() {
        return detailRange;
    }

    public GeometricApproximation getGeometricApproximation() {
        return geometricApproximation;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public int getSides() {
        return sides;
    }

    public Transform getTransform() {
        return transform;
    }

    public float getDisplacementBound() {
        return displacementBound;
    }

    public String getDisplacementBoundCoordinateSystem() {
        return displacementBoundCoordinateSystem;
    }

    public String getObjectIdentifier() {
        return objectIdentifier;
    }

    public TrimCurveSense getTrimCurveSense() {
        return trimCurveSense;
    }

    public Basis getUBasis() {
        return uBasis;
    }
    
    public int getUStep() {
        return uStep;
    }

    public Basis getVBasis() {
        return vBasis;
    }
    
    public int getVStep() {
        return vStep;
    }
    
    public Space getSpace() {
        return space;
    }

    public boolean getTrueDisplacement() {
        return trueDisplacement;
    }

}
