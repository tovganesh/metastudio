/*
 * JRMan.java Copyright (C) 2003 Gerardo Horvilleur Martinez
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.jrman.main;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jrman.maps.MipMap;
import org.jrman.maps.ShadowMap;
import org.jrman.parser.Parser;
import org.jrman.util.Format;

public class JRMan {
	public static int majorNumber= 0;
	public static int minorNumber= 4;
	private static final String MESSAGE_APPNAME= "jrMan";
	private static final String MESSAGE_APPDESCRIPTION= "Java REYES based renderer.";
	private static final String MESSAGE_LICENSE= "This program is free software; you can redistribute it and/or modify\n" + "it under the terms of the GNU General Public License.";
	public static final char OPTION_STATISTICS= 's';
	public static final char OPTION_LASTFRAME= 'e';
	public static final char OPTION_FIRSTFRAME= 'f';
	public static final char OPTION_FRAMEBUFFER= 'd';
	public static final char OPTION_VERSION= 'v';
	public static final char OPTION_HELP= 'h';
	public static final char OPTION_PROGRESS= 'p';
	public static final char OPTION_QUALITY= 'q';
	public static final Options options= prepareOptions();

	public static void main(String args[]) {
		try {
			Options opts= prepareOptions();
			// parse command line
			CommandLine cmdLine= new BasicParser().parse(opts, args);
			// deal with help and version options or no args at all immediately
			if (cmdLine.getArgList().isEmpty() || cmdLine.hasOption(OPTION_HELP)) {
				printUsage(opts);
				System.exit(0);
			}
			if (cmdLine.hasOption(OPTION_VERSION)) {
				printVersion();
				System.exit(0);
			}
			// process arguments (rib files to render)
			Iterator it= cmdLine.getArgList().iterator();
			long totalStart= System.currentTimeMillis();
			while (it.hasNext())
				try {
					String filename= (String) it.next();
					long start= System.currentTimeMillis();
					Parser parser= new Parser(cmdLine);
					parser.begin(filename);
					File file = new File(filename);
					String directory = file.getParent();
					parser.setCurrentDirectory(directory);
					MipMap.setCurrentDirectory(directory);
					ShadowMap.setCurrentDirectory(directory);
					parser.parse(file.getName());
					parser.end();
					long end= System.currentTimeMillis();
					System.out.println(filename + " time: " + Format.time(end - start));
				}
				catch (IOException ioe) {
					System.err.println("Error on file " + ioe.getLocalizedMessage());
				}
				catch (Exception e) {
					System.err.println("Error  " + e.getLocalizedMessage());
				}
			long totalEnd= System.currentTimeMillis();
			System.out.println("Total time: " + Format.time(totalEnd - totalStart));
		}
		catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}
	}

	private static Options prepareOptions() {
		Options options= new Options();
		OptionBuilder.withLongOpt("help");
		OptionBuilder.withDescription("show this usage information and exits");
		options.addOption(OptionBuilder.create(OPTION_HELP));
		OptionBuilder.withLongOpt("version");
		OptionBuilder.withDescription("show application version and exits");
		options.addOption(OptionBuilder.create(OPTION_VERSION));
		OptionBuilder.withLongOpt("display");
		OptionBuilder.withDescription("always show image in a framebuffer display (implicitly add such " + "display if not already present)");
		options.addOption(OptionBuilder.create(OPTION_FRAMEBUFFER));
		OptionBuilder.withLongOpt("progress");
		OptionBuilder.withDescription("show per frame rendering progress");
		options.addOption(OptionBuilder.create(OPTION_PROGRESS));
		OptionBuilder.withLongOpt("first");
		OptionBuilder.withDescription("start rendering from <frame>");
		OptionBuilder.hasArg();
		OptionBuilder.withType(new Integer(1));
		OptionBuilder.withArgName("frame");
		options.addOption(OptionBuilder.create(OPTION_FIRSTFRAME));
		OptionBuilder.withLongOpt("end");
		OptionBuilder.withDescription("stop rendering after <frame>");
		OptionBuilder.hasArg();
		OptionBuilder.withArgName("frame");
		options.addOption(OptionBuilder.create(OPTION_LASTFRAME));
		OptionBuilder.withLongOpt("stats");
		OptionBuilder.withDescription("print end of frame statistics");
		// TODO implement level of detail for rendering statistics
		//OptionBuilder.hasOptionalArg();
		//OptionBuilder.withType(new Integer(1));
		//OptionBuilder.withArgName("level");
		options.addOption(OptionBuilder.create(OPTION_STATISTICS));
		OptionBuilder.withLongOpt("quality");
		OptionBuilder.withDescription("higher rendering quality (slightly slower)");
		options.addOption(OptionBuilder.create(OPTION_QUALITY));
		return options;
	}

	private static void printHeader() {
		System.out.print(MESSAGE_APPNAME);
		System.out.print(" v");
		System.out.print(majorNumber);
		System.out.print('.');
		System.out.println(minorNumber);
		System.out.println(MESSAGE_APPDESCRIPTION);
	}

	private static void printVersion() {
		printHeader();
		System.out.println();
		System.out.println(MESSAGE_LICENSE);
	}

	private static void printUsage(Options options) {
		printHeader();
		System.out.println();
		HelpFormatter formatter= new HelpFormatter();
		formatter.printHelp("jrman [options] [file1.rib] [file2.rib] ...", "Supported options are:", options, "If no rib file is given a basic graphical user interface is presented.");
		System.out.println();
		System.out.println(MESSAGE_LICENSE);
	}

}
