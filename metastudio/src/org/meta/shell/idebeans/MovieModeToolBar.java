/*
 * MovieModeToolBar.java
 *
 * Created on March 10, 2005, 7:05 AM
 */

package org.meta.shell.idebeans;

import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import org.meta.common.resource.ImageResource;
import org.meta.movie.event.MovieUpdateEvent;
import org.meta.movie.event.MovieUpdateListener;

/**
 * The movie mode toolbar.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MovieModeToolBar extends JToolBar {
    
    private JButton play, stop;
    private JLabel currentFrameLabel, timeDelayLabel, secondLabel;
    private JSpinner timeDelay;
    private JCheckBox repeatMovie;
    private JSlider currentFrame;
    private MoleculeViewerFrame moleculeViewerFrame;
        
    private boolean playMode, resumeMode;
    
    private boolean programaticUpdate;
    
    /** Creates a new instance of MovieModeToolBar */
    public MovieModeToolBar(MoleculeViewerFrame moleculeViewerFrame) {
        this.moleculeViewerFrame = moleculeViewerFrame;
        this.playMode = this.resumeMode = false;
        
        makeToolBar();
        
        programaticUpdate = false;
        
        // and a callback for movie events
        moleculeViewerFrame.getMoleculeViewer().addMovieUpdateListener(
            new MovieUpdateListener() {
                public void  movieUpdate(MovieUpdateEvent me) {
                    programaticUpdate = true;
                    currentFrame.setValue(me.getCurrentMovieScene());
                    programaticUpdate = false;
                    
                    if (me.getMovieStatus() 
                        == MovieUpdateEvent.MovieStatus.STOPED) {
                        playMode = resumeMode = false;
                        play.setIcon(ImageResource.getInstance().getPlay());
                        play.setToolTipText("Play");
                        stop.setEnabled(false);
                    } // end if
                }
            }
        );
    }
    
    /**
     * Make the tool bar
     */
    private void makeToolBar() {
        ImageResource images = ImageResource.getInstance();
        
        // add main movie controls
        play = new JButton(images.getPlay());
        play.setToolTipText("Play");
        play.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (!playMode) {
                    moleculeViewerFrame.getMoleculeViewer().beginMovie();
                    currentFrame.setMinimum(0);
                    currentFrame.setMaximum(moleculeViewerFrame
                            .getMoleculeViewer().getSceneList().size()-1);
                    currentFrame.setMinorTickSpacing(1);
                    currentFrame.setMajorTickSpacing(1);
                    playMode = true;                    
                    play.setIcon(ImageResource.getInstance().getPause());
                    play.setToolTipText("Pause");
                    
                    // enable controls
                    stop.setEnabled(true);
                } else {
                    if (!resumeMode) {
                        moleculeViewerFrame.getMoleculeViewer().pauseMovie();
                        play.setIcon(ImageResource.getInstance().getPlay());
                        play.setToolTipText("Resume");
                        resumeMode = true;
                    } else {
                        moleculeViewerFrame.getMoleculeViewer().resumeMovie();
                        play.setIcon(ImageResource.getInstance().getPause());
                        play.setToolTipText("Pause");
                        resumeMode = false;
                    } // end if
                } // end if
            }
        });
        add(play);
        
        stop = new JButton(images.getStop());
        stop.setToolTipText("Stop");
        stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                moleculeViewerFrame.getMoleculeViewer().endMovie();
                playMode = resumeMode = false;
                play.setIcon(ImageResource.getInstance().getPlay());
                play.setToolTipText("Play");
                stop.setEnabled(false);
            }
        });
        stop.setEnabled(false);
        add(stop);
        
        addSeparator();
        
        // add additional movie controls
        currentFrameLabel = new JLabel("Current Frame: ");
        add(currentFrameLabel);
        
        currentFrame = new JSlider();
        currentFrame.setToolTipText("Choose the current frame to view");
        currentFrame.setPaintLabels(true);
        currentFrame.setPaintTicks(true);
        currentFrame.setPaintTrack(true);
        currentFrame.setSnapToTicks(true);
        currentFrame.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                int f = ((Integer) currentFrame.getValue()).intValue();
                currentFrameLabel.setText("Current Frame [" + f + "]: ");
                
                if (programaticUpdate) return;
                
                moleculeViewerFrame.getMoleculeViewer().setCurrentMovieScene(f);
            }
        });
        add(currentFrame);
        
        timeDelayLabel = new JLabel("Time Delay: ");
        add(timeDelayLabel);
        
        timeDelay = new JSpinner(new SpinnerNumberModel(
          moleculeViewerFrame.getMoleculeViewer().getMovieDelay(), 1, 9999, 1));
        timeDelay.setToolTipText("Set time delay between viewing two frames " +
                                 "in a movie");
        timeDelay.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                moleculeViewerFrame.getMoleculeViewer().setMovieDelay(
                        ((Integer) timeDelay.getValue()).intValue());
            }
        });
        add(timeDelay);
        
        secondLabel = new JLabel("sec. ");
        add(secondLabel);
        
        repeatMovie = new JCheckBox("Repeat Movie");
        repeatMovie.setToolTipText("Repeat the movie in a loop");
        repeatMovie.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                moleculeViewerFrame.getMoleculeViewer().setRepeatMovie(
                                                     repeatMovie.isSelected());
            }
        });
        add(repeatMovie);
    }
} // end of class MovieModeToolBar
