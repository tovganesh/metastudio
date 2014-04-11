/*
 MutableAttributes.java
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.vecmath.Color3f;

import org.jrman.geom.AffineTransform;
import org.jrman.geom.Bounds3f;
import org.jrman.geom.Transform;
import org.jrman.shaders.DisplacementShader;
import org.jrman.shaders.LightShader;
import org.jrman.shaders.SurfaceShader;
import org.jrman.shaders.VolumeShader;

public class MutableAttributes extends Attributes {

    private boolean modified;

    public MutableAttributes() {
        color = new Color3f(1f, 1f, 1f);
        opacity = new Color3f(1f, 1f, 1f);
        textureCoordinates = null;
        lightSources = Collections.emptySet();
        surface = null;
        displacement = null;
        atmosphere = null;
        interior = null;
        exterior = null;
        shadingRate = 1f;
        shadingInterpolation = ShadingInterpolation.CONSTANT;
        matte = false;
        detail = Bounds3f.ALL;
        geometricApproximation =
            new GeometricApproximation(GeometricApproximation.Type.FLATNESS, 1f);
        orientation = Orientation.OUTSIDE;
        sides = 2;
        transform = AffineTransform.IDENTITY;
        displacementBound = 0f;
        displacementBoundCoordinateSystem = "object";
        objectIdentifier = null;
        trimCurveSense = TrimCurveSense.INSIDE;
        uBasis = Basis.BEZIER;
        uStep = 3;
        vBasis = Basis.BEZIER;
        vStep = 3;
        space = Space.CAMERA;
        trueDisplacement = true;
    }

    public MutableAttributes(Attributes other) {
        super(other);
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public void setColor(Color3f color) {
        this.color = new Color3f(color);
        modified = true;
    }

    public void setOpacity(Color3f opacity) {
        this.opacity = new Color3f(opacity);
        modified = true;
    }

    public void setTextureCoordinates(TextureCoordinates textureCoordinates) {
        this.textureCoordinates = textureCoordinates;
        modified = true;
    }

    public void setLightSources(Set<LightShader> lightSources) {
        this.lightSources = new HashSet<LightShader>(lightSources);
        modified = true;
    }

    public void setSurface(SurfaceShader surface) {
        this.surface = surface;
        modified = true;
    }

    public void setDisplacement(DisplacementShader displacement) {
        this.displacement = displacement;
        modified = true;
    }

    public void setAtmosphere(VolumeShader atmosphere) {
        this.atmosphere = atmosphere;
        modified = true;
    }

    public void setInterior(VolumeShader interior) {
        this.interior = interior;
        modified = true;
    }

    public void setExterior(VolumeShader exterior) {
        this.exterior = exterior;
        modified = true;
    }

    public void setShadingRate(float shadingRate) {
        this.shadingRate = shadingRate;
        modified = true;
    }

    public void setShadingInterpolation(ShadingInterpolation shadingInterpolation) {
        this.shadingInterpolation = shadingInterpolation;
        modified = true;
    }

    public void setMatte(boolean matte) {
        this.matte = matte;
        modified = true;
    }

    public void setBound(Bounds3f bound) {
        this.bound = bound;
        modified = true;
    }

    public void setDetail(Bounds3f detail) {
        this.detail = detail;
        modified = true;
    }

    public void setDetailRange(DetailRange detailRange) {
        this.detailRange = detailRange;
        modified = true;
    }

    public void setGeometricApproximation(GeometricApproximation geometricApproximation) {
        this.geometricApproximation = geometricApproximation;
        modified = true;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
        modified = true;
    }

    public void setSides(int sides) {
        this.sides = sides;
        modified = true;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
        modified = true;
    }

    public void setDisplacementBound(float displacementBound) {
        this.displacementBound = displacementBound;
        modified = true;
    }

    public void setDisplacementBoundCoordinateSystem(String displacementBoundCoordinateSystem) {
        this.displacementBoundCoordinateSystem = displacementBoundCoordinateSystem;
        modified = true;
    }

    public void setObjectIdentifer(String objectIdentifier) {
        this.objectIdentifier = objectIdentifier;
        modified = true;
    }

    public void setTrimCurveSense(TrimCurveSense trimCurveSense) {
        this.trimCurveSense = trimCurveSense;
        modified = true;
    }

    public void setBasis(Basis uBasis, int uStep, Basis vBasis, int vStep) {
        this.uBasis = uBasis;
        this.uStep = uStep;
        this.vBasis = vBasis;
        this.vStep = vStep;
        modified = true;
    }
    
    public void setSpace(Space space) {
        this.space = space;
        modified = true;
    }

    public void setTrueDisplacement(boolean v) {
        trueDisplacement = v;
        modified = true;
    }

}
