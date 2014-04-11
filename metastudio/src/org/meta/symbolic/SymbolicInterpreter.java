/**
 * SymbolicInterpreter.java
 *
 * Created on 01/09/2009
 */

package org.meta.symbolic;

import java.util.Stack;
import org.meta.math.geom.Point3D;
import org.meta.common.Utility;
import org.meta.molecule.Atom;
import org.meta.molecule.Molecule;
import org.meta.molecule.MoleculeBuilder;

/**
 * The symbolic interpreter for MeTA Studio symbolic processing language. <br>
 * A symbolic MeTA Studio statement is generally of the form:
 * <pre>
 * [var name] = F[arg1, arg2, ...]
 * </pre>
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class SymbolicInterpreter {

    protected Stack<Object> resultStack;

    /** Creates a new instance of SymbolicInterpreter */
    public SymbolicInterpreter() {
        resultStack = new Stack<Object>();
    }

    /**
     * Evaluate a symbolic expression
     *
     * @param symbolStr the expression
     * @return the value at the top of stack after execution of this statement.
     */
    public Object eval(String symbolStr) {
        // TODO: Just a demo!
        if (symbolStr.equals("Molecule[H2]")) {
            Molecule mol = Utility.createMoleculeObject();

            mol.setTitle("H2");
            mol.addAtom(new Atom("H", 0, new Point3D()));
            mol.addAtom(new Atom("H", 0,
                new Point3D(Utility.AU_TO_ANGSTROM_FACTOR, 0, 0)));

            MoleculeBuilder mb = Utility.createMoleculeBuilderObject();
            mb.makeSimpleConnectivity(mol);

            resultStack.add(mol);

            return resultStack.peek();
        } // end if
        
        return null;
    }
}
