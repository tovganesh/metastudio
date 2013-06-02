/*
 Transform.java
 Copyright (C) 2003 Gerardo Horvilleur Martinez

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

package org.jrman.geom;

import java.io.DataOutputStream;
import java.io.IOException;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;

public interface Transform {
    
    Transform getCopy();
    
    Transform concat(Transform transform);

    Transform concat(Matrix4f matrix);
    
    void transformPoint(Point2f point);
    
    void transformPoint(Point2f point, Point2f out);
    
    void transformPoint(Point3f point);
    
    void transformPoint(Point3f point, Point3f out);
    
    void transformHPoint(Point4f hpoint);
    
    void transformHPoint(Point4f hpoint, Point4f out);
    
    void transformVector(Vector3f vector);
    
    void transformVector(Vector3f vector, Vector3f out);
    
    void transformNormal(Vector3f normal);
    
    void transformNormal(Vector3f normal, Vector3f out);
    
    void untransformPoint(Point2f point);
    
    void untransformPoint(Point2f point, Point3f out);
    
    void untransformPoint(Point3f point);
    
    void untransformPoint(Point3f point, Point3f out);
    
    void untransformHPoint(Point4f hpoint);
    
    void untransformHPoint(Point4f hpoint, Point4f out);
    
    void untransformVector(Vector3f vector);
    
    void untransformVector(Vector3f vector, Vector3f out);
    
    void untransformNormal(Vector3f normal);
    
    void untransformNormal(Vector3f normal, Vector3f out);
    
    Transform getInverse();
    
    Matrix4f getMatrix();
    
    boolean isPerspective();
    
    void writeToFile(DataOutputStream dos) throws IOException;
    
}
