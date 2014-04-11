/**
 * ScreenAtomJ2DImpl.java
 *
 * Created on Jun 16, 2009
 */
package org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.j2d;

import org.meta.molecule.Atom;
import org.meta.shell.idebeans.graphics.PaintGlyphObject;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.ScreenAtom;

/**
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ScreenAtomJ2DImpl extends ScreenAtom {

    /** Creates a new instance of ScreenAtom */
    public ScreenAtomJ2DImpl(Atom atom) {
        super(atom);
    }

    /**
     *
     * Generic paint method for this glyph.
     *
     * @param pgo The instance of PaintGlyphObject that will actually provide
     *            primitives to render this glyph.
     */
    @Override
    public void paintGlyph(PaintGlyphObject pgo) {
        // default implementation does nothing
    }
}
