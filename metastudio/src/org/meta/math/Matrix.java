/*
 * Matrix.java
 *
 * Created on August 1, 2004, 7:53 PM
 */

package org.meta.math;

import java.text.DecimalFormat;
import org.meta.math.la.Diagonalizer;
import org.meta.math.la.DiagonalizerFactory;

/**
 * A general NxM real matrix.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class Matrix implements Cloneable {
    
    /**
     * Holds value of property matrix.
     */
    protected double[][] matrix;
    
    /**
     * Holds value of property rowCount.
     */
    protected int rowCount;
    
    /**
     * Holds value of property columnCount.
     */
    protected int columnCount;
    
    /** 
     * Creates a new instance of NxM Matrix 
     *
     * @param n the first dimension
     * @param m the second dimension
     */
    public Matrix(int n, int m) {
        matrix = new double[n][m];
        
        rowCount    = n;
        columnCount = m;
    }
    
    /** 
     * Creates a new instance of square (NxN) Matrix
     *
     * @param n the dimension 
     */
    public Matrix(int n) {
        this(n, n);
    }
    
    /** 
     * Creates a new instance of Matrix, based on already allocated 2D array
     *
     * @param a the 2D array
     */
    public Matrix(double [][] a) {
        matrix = a;
        
        rowCount    = a.length;
        columnCount = a[0].length;
    }
    
    /**
     * Creates a diagonal matrix from a Vector
     * 
     * @param vec the Vector
     */
    public Matrix(Vector vec) {
        this(vec.getSize());

        for(int i=0; i<matrix.length; i++) matrix[i][i] = vec.getVector(i);
    }

    /**
     * Simple matrix addition of two square matrices: this + b
     * 
     * @param b the matrix to which to add
     * @return the result Cij = (Aij + Bij)
     */
    public Matrix add(Matrix b) {
        Matrix c = new Matrix(rowCount);
        
        int i, j;        
        for(i=0; i<rowCount; i++) {
            for(j=0; j<columnCount; j++) {                
                c.matrix[i][j] = matrix[i][j] + b.matrix[i][j];
            } // end for
        } // end for
        
        return c;
    }

    /**
     * Simple matrix substraction of two square matrices: this - b
     *
     * @param b the matrix to which to substract
     * @return the result Cij = (Aij - Bij)
     */
    public Matrix sub(Matrix b) {
        Matrix c = new Matrix(rowCount);

        int i, j;
        for(i=0; i<rowCount; i++) {
            for(j=0; j<columnCount; j++) {
                c.matrix[i][j] = matrix[i][j] - b.matrix[i][j];
            } // end for
        } // end for

        return c;
    }
    
    /**
     * Simple matrix multiplication of two matrices: this * b
     * 
     * @param b the matrix to which to multiply
     * @return the result Cij = Sum(Aik*Bkj)
     */
    public Matrix mul(Matrix b) {
        Matrix c = new Matrix(rowCount, b.columnCount);
        
        int i, j, k;        
        double cij;
        for(i=0; i<rowCount; i++) {
            for(j=0; j<b.columnCount; j++) {
                cij = 0.0;
                for(k=0; k<b.rowCount; k++) {
                    cij += matrix[i][k] * b.matrix[k][j];
                } // end for
                c.matrix[i][j] = cij;
            } // end for
        } // end for
        
        return c;
    }

    /**
     * Defines a vector '.' operation on two matrices, 'as if'
     * flattened out as NxM size vector. Both the matrices need to
     * of same size to give correct result. No checks are made to
     * make sure this is the case.
     *
     * @param b the matrix to do a dot product
     * @return the value of dot product
     */
    public double dot(Matrix b) {
        int i, j;
        double res = 0.0;

        for(i=0; i<rowCount; i++) {
            for(j=0; j<columnCount; j++) {
                res += matrix[i][j]*b.matrix[i][j];
            } // end for
        } // end for

        return res;
    }
    
    /**
     * Transpose a real matrix A (A')
     *     
     * @return the transposed instance
     */
    public Matrix transpose() {
        Matrix matrixT = new Matrix(columnCount, rowCount);
        
        int i, j;
        for(i=0; i<rowCount; i++) {
            for(j=0; j<columnCount; j++) {
                matrixT.matrix[j][i] = matrix[i][j];
            } // end for
        } // end for
        
        return matrixT;
    }
    
    /**
     * Tr(this) = sum(diagonal(this)); Only valid if this a square matrix.
     *
     * @return the trace of this matrix
     */
    public double trace() {
        double tr = 0.0;
        
        for(int i=0; i<rowCount;i++) tr += matrix[i][i];
        
        return tr;
    }
    
    /**
     * Similarity Transform : <code> XAX' :: x * this * x' </code>
     *
     * @param x the pre multiplier
     * @return XAX'
     */
    public Matrix similarityTransform(Matrix x) {
        return x.mul(this).mul(x.transpose());
    }
    
    /**
     * Similarity Transform : <code> X'AX :: x' * this * x </code>
     *
     * @param x the post multiplier
     * @return X'AX
     */
    public Matrix similarityTransformT(Matrix x) {
        return x.transpose().mul(this).mul(x);
    }
    
    /**
     * Symmetric orthogonalization of the real symmetric matrix X (this).
     * This is given by <code>U'(1/sqrt(lambda))U</code>, where lambda, 
     * U are the eigenvalues/vectors
     *
     * @return a matrix object U'(1/sqrt(lambda))U
     */
    public Matrix symmetricOrthogonalization() {
        Diagonalizer diag = DiagonalizerFactory.getInstance()
                                               .getDefaultDiagonalizer();
        
        diag.diagonalize(this);
        
        double [] eigenValues = diag.getEigenValues();
        Matrix eigenVectors   = diag.getEigenVectors();
        Matrix sHalf          = new Matrix(rowCount);
        
        sHalf.makeIdentity();
        for(int i=0; i<rowCount; i++) {
            sHalf.matrix[i][i] /= Math.sqrt(eigenValues[i]);
        } // end for
        
        return (sHalf.similarityTransformT(eigenVectors));
    }
    
    /**
     * make the current matrix an identity matrix, all diagonals as 1.0 
     * and non-diagonals zero<br>
     * only sensible if a square matrix
     */
    public void makeIdentity() {
        int i, j;
        for(i=0; i<rowCount; i++) {
            for(j=0; j<i; j++) {
                matrix[i][j] = matrix[j][i] = 0.0;
            } // end for
            matrix[i][i] = 1.0;
        } // end for
    }

    /**
     * init the current matrix to zero
     */
    public void makeZero() {
        int i, j;
        for(i=0; i<rowCount; i++) {
            for(j=0; j<columnCount; j++) {
                matrix[i][j] = 0.0;
            } // end for
        } // end for
    }

    /**
     * rootMeanSquare() - method to compute the root mean square of
     *                    elements of a diagonal. This is one type of matrix 
     *                    norm. <code>sqrt(sum(A<sub>ii</sub>))<code>.
     *
     * @return double - the root mean square of the elements of the diagonal.
     */    
    public double rootMeanSquare() {
        double sum = 0.0;
        
        for(int i=0; i<rowCount; i++) {
            sum += matrix[i][i] * matrix[i][i]; // sum of squares
        } // end for
        
        return Math.sqrt(sum/rowCount); // and its sqrt
    }
    
    /**
     * Absolute sum of off-diagonal elements.
     *
     * @return Sum(a[i][j], i!=j)
     */
    public double sumOffDiagonal() {
        double sum = 0.0;
        int i, j;
        
        for(i=0; i<rowCount-1; i++) {
            for(j=i+1; j<rowCount; j++) {
                sum += Math.abs(matrix[i][j]);
            } // end for
        } // end for
        
        return sum;         
    }
    
    /**
     * Getter for property matrix.
     * @return Value of property matrix.
     */
    public double[][] getMatrix() {
        return this.matrix;
    }
    
    /**
     * Getter for property matrix.
     *
     * @param i index i
     * @param j index j
     *
     * @return Value of property matrix.
     */
    public double getMatrixAt(int i, int j) {
        return this.matrix[i][j];
    }
    
    /**
     * Setter for property matrix.
     * @param matrix New value of property matrix.
     */
    public void setMatrix(double[][] matrix) {
        this.matrix = matrix;
        
        rowCount    = matrix.length;
        columnCount = matrix[0].length;
    }
    
    /**
     * Getter for property matrix.
     *
     * @param i index i
     * @param j index j
     * @param value the new value    
     */
    public void setMatrixAt(int i, int j, double value) {
        this.matrix[i][j] = value;
    }

    /**
     * Convert this Matrix object to a Vector object
     *
     * @return the "flattened" Vector object
     */
    public Vector toVector() {
        Vector vec = new Vector(rowCount*columnCount);
        double [] vecval = vec.getVector();
        int i, j, ii=0;

        for(i=0; i<rowCount; i++)
            for(j=0; j<columnCount; j++)
                vecval[ii++] = this.matrix[i][j];
        
        return vec;
    }

    /**
     * overridden toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        DecimalFormat df = new DecimalFormat("#.#######");
        df.setMinimumFractionDigits(8);
        
        int i, j;
        for(i=0; i<rowCount; i++) {
            for(j=0; j<columnCount; j++) {
                sb.append(df.format(matrix[i][j]) + " ");
            } // end for
            sb.append('\n');
        } // end for
        
        return sb.toString();
    }
    
    /**
     * clone this vector ;)
     * Cloning is getting interesting! :)
     *
     * @return the clone
     */
    @Override
    public Object clone() {
        Matrix theCopy = new Matrix(rowCount, columnCount);
        
        int i, j;
        for(i=0; i<rowCount; i++) {
            for(j=0; j<columnCount; j++) {
                theCopy.matrix[i][j] = matrix[i][j];
            } // end for
        } // end for
        
        theCopy.rowCount    = rowCount;
        theCopy.columnCount = columnCount;
        
        return theCopy;
    }
    
    /**
     * get a row vector 
     *
     * @param rowIndex - the row index to obtain
     * @return instance of Vector representing the requested row
     */
    public Vector getRowVector(int rowIndex) {
        Vector row = new Vector(columnCount);
    
        double [] r = row.getVector();
        
        for(int i=0; i<columnCount; i++) r[i] = matrix[rowIndex][i];
        
        return row;
    }
    
    /**
     * get a column vector 
     *
     * @param colIndex - the column index to obtain
     * @return instance of Vector representing the requested column
     */
    public Vector getColumnVector(int colIndex) {
        Vector column = new Vector(rowCount);
    
        double [] c = column.getVector();
        
        for(int i=0; i<rowCount; i++) c[i] = matrix[i][colIndex];
        
        return column;
    }
    
    /**
     * Getter for property rowCount.
     * @return Value of property rowCount.
     */
    public int getRowCount() {
        return this.rowCount;
    }
    
    /**
     * Setter for property rowCount.
     * @param rowCount New value of property rowCount.
     */
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }
    
    /**
     * Getter for property columnCount.
     * @return Value of property columnCount.
     */
    public int getColumnCount() {
        return this.columnCount;
    }
    
    /**
     * Setter for property columnCount.
     * @param columnCount New value of property columnCount.
     */
    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    /**
     * Is this upper triangular matrix?
     *
     * @return true is upper triangular, false otherwise
     */
    public boolean isUpperTriangular() {
        if (this.columnCount != this.rowCount) return false;

        for(int i=0; i<matrix.length; i++) {
            for(int j=0; j<i; j++) {
                if (matrix[i][j] != 0) {
                    return false;
                } // end if
            } // end for
        } // end for

        return true;
    }

    /**
     * isSingular() - This method finds whether a matrix is singular or not i.e
     *                if pth row contains all zeros.
     *
     * @param  p - The row to be checked for zero.
     * @return boolean
     *              - true if the matrix is singular
     *              - false otherwise
     */
    public boolean isSingular(int p) {
        for(int i=p; i<=matrix[p].length; i++) {
            if (matrix[p][i] != 0) {
                return false;
            } // end if
        } // end for
        
        return true;
    }

    /**
     * isSingular() - This method finds whether a matrix is singular or not i.e
     *                if pth row contains all zeros.
     *
     * @param p - The row to be checked for zero.
     * @param row - modified row indices 
     * @return boolean
     *              - true if the matrix is singular
     *              - false otherwise
     */
    public boolean isSingular(int p, int [] row) {
        if (row.length != matrix[row[p]].length)
            throw new UnsupportedOperationException(
                                 "Size of row index and matrix do no match!");

        for(int i=p; i<=matrix[0].length-1; i++) {
            if (matrix[row[p]][i] != 0) {
                return false;
            } // end if
        } // end for

        return true;
    }

    /**
     * Returns the max norm of this matrix
     *
     * @return max(abs(matrix))
     */
    public double maxNorm() {
        double mx = Math.abs(matrix[0][0]);

        for(int i=0; i<rowCount; i++)
            for(int j=0; j<columnCount; j++)
                if (mx < Math.abs(matrix[i][j]))
                    mx = Math.abs(matrix[i][j]);

        return mx;
    }

    /**
     * Return the scaled instance of this matrix, each element
     * multiplied by the scale factor
     *
     * @param scaleFactor the scale factor with which each element is multiplied
     * @return the scaled instance of the current matrix
     */
    public Matrix mul(double scaleFactor) {
        Matrix res = new Matrix(rowCount, columnCount);

        for(int i=0; i<rowCount; i++)
            for(int j=0; j<columnCount; j++)
                res.matrix[i][j] = scaleFactor * matrix[i][j];

        return res;
    }
} // end of class
