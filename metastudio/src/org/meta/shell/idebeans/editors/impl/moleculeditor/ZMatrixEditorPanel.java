/*
 * ZMatrixEditorPanel.java 
 *
 * Created on 24 Jul, 2008 
 */

package org.meta.shell.idebeans.editors.impl.moleculeditor;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import org.meta.molecule.Atom;
import org.meta.molecule.Molecule;
import org.meta.shell.idebeans.SidePanel;

/**
 * Z-Matrix editor for the molecule editor
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ZMatrixEditorPanel extends SidePanel {

    private JTable zMatrixTable;
    private TableModel zMatrixDataModel;
            
    private MoleculeEditor editor;
    
    
    /** Creates instance of ZMatrixEditorPanel */
    public ZMatrixEditorPanel(MoleculeEditor editor) {
        this.editor = editor;
        
        initTableData();      
        zMatrixTable = new JTable(zMatrixDataModel);
        getContentPane().add(new JScrollPane(zMatrixTable));
    }       
    
    /** initilize the table data */
    public void initTableData() {
        zMatrixDataModel = new AbstractTableModel() {
          @Override
          public int getColumnCount() { 
              return 7; 
          }
          
          @Override
          public int getRowCount() { 
              return ZMatrixEditorPanel.this.editor.getSceneList()
                            .get(0).getMolecule().getNumberOfAtoms();
          }
          
          @Override
          public Object getValueAt(int row, int col) { 
               Molecule mol = ZMatrixEditorPanel.this.editor.getSceneList()
                                                          .get(0).getMolecule();
               Atom atom = mol.getAtom(row);
               Atom ra;
               
               switch(col) {
                   case 0:
                       return atom.getSymbol() + atom.getIndex();
                   case 1:
                       if (row == 0) return "";
                       ra = atom.getLengthReference().getReferenceAtom();
                       return ra.getSymbol() + ra.getIndex();
                   case 2:                       
                       if (row == 0) return "";
                       return atom.getLengthReference().getValue();
                   case 3:
                       if (row <= 1) return "";
                       ra = atom.getAngleReference().getReferenceAtom();
                       return ra.getSymbol() + ra.getIndex();
                   case 4:
                       if (row <= 1) return "";
                       return atom.getAngleReference().getValue();
                   case 5:
                       if (row <= 2) return "";
                       ra = atom.getDihedralReference().getReferenceAtom();
                       return ra.getSymbol() + ra.getIndex();
                   case 6:
                       if (row <= 2) return "";
                       return atom.getDihedralReference().getValue();
                   default:
                       return "";
               } // end switch .. case
          }
      };
    }
}
