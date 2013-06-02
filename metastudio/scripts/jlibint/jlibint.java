import com.sun.jna.*;

import org.meta.math.qm.*;
import org.meta.math.qm.basis.*;
import org.meta.math.qm.integral.*; 


interface LibInt extends Library {
    public void init_libint_base();
    public int libint_storage_required(int max_am, int max_num_prim_comb);

    public class prim_data extends Structure {
       public double [] F = new double[17];  // 17
       public double [][] U = new double[6][3]; // 6, 3
       public double twozeta_a;
       public double twozeta_b;
       public double twozeta_c;
       public double twozeta_d;
       public double oo2z;
       public double oo2n;
       public double oo2zn;
       public double poz;
       public double pon;
       public double oo2p;
       public double ss_r12_ss;

       public static class ByReference extends prim_data implements Structure.ByReference { }
    }

    public class Libint_t extends Structure {
       public Pointer int_stack;
       public Pointer PrimeQuartet;
       public double [] AB = new double[3]; // 3
       public double [] CD = new double[3]; // 3
       public Pointer [][] vrr_classes = new Pointer[9][9]; // 9, 9
       public Pointer vrr_stack;

       public static class ByReference extends Libint_t implements Structure.ByReference { }
    }    

    public int init_libint(Libint_t.ByReference lb, int max_am, int max_num_prim_comb);
    public void free_libint(Libint_t.ByReference lb);
}

class LibIntTwoE {
    private LibInt libInt;

    public LibIntTwoE() { 
       LibInt libInt = (LibInt) Native.loadLibrary("int", LibInt.class);
       libInt.init_libint_base();
       System.out.println(libInt.libint_storage_required(4, (int) Math.pow(15, 4)));

       LibInt.Libint_t.ByReference lb = new LibInt.Libint_t.ByReference();

       lb.PrimeQuartet  = (new LibInt.prim_data()).getPointer();

       System.out.println(libInt.init_libint(lb, 4, (int) Math.pow(15, 4)));
    }

    public double [] columbBlock() { 
       double [] ints = new double[0];

       return ints;
    }
}

public class jlibint {
  public static void main(String [] args) {
     LibIntTwoE twoEI = new LibIntTwoE();
  }
}

