/*
 * ImageTalkObject.java 
 *
 * Created on 12 Oct, 2008 
 */

package org.meta.net.impl.service.talk;

import java.awt.image.BufferedImage;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import javax.imageio.ImageIO;

/**
 * Encapsulates an image object as a talk object that can be transported over
 * a network i/o stream.
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ImageTalkObject extends TalkObject 
                             implements Externalizable {

    /** Creates an instance of ImageTalkObject */
    public ImageTalkObject() {
        setType(TalkObjectType.IMAGE);
    }
    
    /**
     * Write the image object.
     *
     * @throws IOException if error occured
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        ImageIO.write((BufferedImage) talkObjectContent, "png", 
                      (ObjectOutputStream) out);        
    }

    /**
     * Read the image object.
     *
     * @throws IOException if error occured
     * @throws ClassNotFoundException missing Molecule class?
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, 
                                                    ClassNotFoundException {
        talkObjectContent = ImageIO.read((ObjectInputStream) in);
    }

}
