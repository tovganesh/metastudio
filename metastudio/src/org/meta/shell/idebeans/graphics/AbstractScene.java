/**
 * AbstractScene.java
 *
 * Created on 10/08/2009
 */

package org.meta.shell.idebeans.graphics;

import java.util.ArrayList;

/**
 * Implements a skeletal Scene interface
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class AbstractScene extends AbstractGlyph implements Scene {

    protected ArrayList<Glyph> glyphList;
    
    /** Creates a new instance of AbstractScene */
    public AbstractScene() {
        glyphList = new ArrayList<Glyph>();
    }

    /**
     * Add a Glyph object to this Scene, this infact could be another scene
     * object. Nested Scenes are allowed.
     *
     * @param glyph the Glyph object to be added to this scene
     */
    @Override
    public void addGlyph(Glyph glyph) {
        glyphList.add(glyph);
    }

    /**
     * Remove a Glyph object from this Scene.
     *
     * @param glyph the Glyph object to be removed from this scene
     */
    @Override
    public void removeGlyph(Glyph glyph) {
        glyphList.remove(glyph);
    }

    /**
     * Get a list of Glyph objects
     *
     * @return an Iterable list of
     */
    @Override
    public Iterable<Glyph> getAllGlyphs() {
        return glyphList;
    }
}
