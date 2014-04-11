/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.shell.ide;

import javax.swing.SwingUtilities;

/**
 * Entry point for IDE / daemon / scripts / adding external libs
 * etc.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MeTApp {

    /** Create an instance of MeTApp */
    public MeTApp() {
    }

    /**
     * The main entry point!
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        if (args.length == 0) {
            try {
                // try to start the new launcher
                new MeTALauncher();
            } catch (Exception e) {
                // if it fails ... fall back to a normal start
                final Exception e1 = e;
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                      (new MeTA()).getWorkspaceLog().appendWarning("Unable to " +
                          "spawn MeTA Studio launcher; defaulting to normal " +
                          "startup. Reason: " + e1.toString());   
                    }
                });
                        
                e.printStackTrace();        
            } // end of try catch .. block
        } else {
            if (args[0].equals("--morememory")) {
                // Note: SwingUtilities.invokeLater is used to launch the GUI due to the
                // http://docs.oracle.com/javase/tutorial/uiswing/concurrency/initial.html
                // http://bitguru.wordpress.com/2007/03/21/will-the-real-swing-single-threading-rule-please-stand-up/
                // Thanks to @csrins for pointing this out.

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new MeTA();
                    }
                });
            } else if (args[0].equals("--daemon")) {
                try {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            new MeTA(true);
                        }
                    });
                } catch (Throwable ignored) {
                    System.err.println("Unable to start MeTA Studio in" +
                            " normal daemon mode: " + ignored);
                    System.err.println("Starting in alternative way...");
                    new MeTAPureDaemon();
                } // end if
            } else if (args[0].equals("--script")) {
                try {
                    new MeTAPureDaemon(args[1]);
                } catch (Exception ignored) {
                    System.err.println("Unable to start MeTA Studio in" +
                            " normal daemon mode: " + ignored);
                    System.err.println("Errors are: ");
                    ignored.printStackTrace();
                } // end if
            } else if (args[0].equals("--ndscript")) {
                try {
                    new MeTAPureDaemon(args[1], false);
                } catch (Exception ignored) {
                    System.err.println("Unable to start MeTA Studio in" +
                            " script execution mode: " + ignored);
                    System.err.println("Errors are: ");
                    ignored.printStackTrace();
                } // end if
            } else if (args[0].equals("--mar")) {
                try {
                    new MeTAPureDaemon(args[1], true, true);
                } catch (Exception ignored) {
                    System.err.println("Unable to start MeTA Studio in" +
                            " script execution mode: " + ignored);
                    System.err.println("Errors are: ");
                    ignored.printStackTrace();
                } // end if
            } else if (args[0].equals("--ndmar")) {
                try {
                    new MeTAPureDaemon(args[1], false, true);
                } catch (Exception ignored) {
                    System.err.println("Unable to start MeTA Studio in" +
                            " script execution mode: " + ignored);
                    System.err.println("Errors are: ");
                    ignored.printStackTrace();
                } // end if
            } else if (args[0].equals("--addlibs")) {
                try {
                    MeTAPureDaemon.addLibs(args);
                } catch (Exception ignored) {
                    System.err.println("Unable to add libraries to MeTA Studio"
                            + " installation: " + ignored);
                    System.err.println("Errors are: ");
                    ignored.printStackTrace();
                } // end if
            } else {
                System.err.println("Invalid option: " + args[0]);
                System.err.println("Cannot start MeTA Studio!");
                System.exit(10);
            } // end if
        } // end if
    }
}
