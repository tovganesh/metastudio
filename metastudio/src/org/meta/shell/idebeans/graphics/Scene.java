/**
 * Scene.java
 *
 * Created on 10/08/2009
 */

package org.meta.shell.idebeans.graphics;

/**
 * A Scene is a Glyph that acts as a place holder of other Glyphs
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface Scene extends Glyph {

    /**
     * Add a Glyph object to this Scene, this infact could be another scene
     * object. Nested Scenes are allowed.
     *
     * @param glyph the Glyph object to be added to this scene
     */
    public void addGlyph(Glyph glyph);

    /**
     * Remove a Glyph object from this Scene.
     *
     * @param glyph the Glyph object to be removed from this scene
     */
    public void removeGlyph(Glyph glyph);

    /**
     * Get a list of Glyph objects
     * 
     * @return an Iterable list of
     */
    public Iterable<Glyph> getAllGlyphs();
}
