/*
 * $Id$
 *
 * Authors:
 *      Jeff Buchbinder <jeff@freemedsoftware.org>
 *
 * FreeMED Electronic Medical Record and Practice Management System
 * Copyright (C) 1999-2010 FreeMED Software Foundation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package org.freemedsoftware.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class CompositePdfForm {

	public static String VERSION = "0.1";

	public static void main(String[] args) throws Exception {
		CommandLineParser parser = new GnuParser();
		CommandLine line = null;
		try {
			line = parser.parse(getOptions(), args);
		} catch (ParseException exp) {
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
			System.exit(1);
		}

		String dataFile = line.getOptionValue("i");
		String templateFile = line.getOptionValue("t");
		String outputFile = line.getOptionValue("o");

		if (dataFile == null || templateFile == null || outputFile == null) {
			System.err.println("options:");
			System.err.println("\t[-i|--input]     Input file name");
			System.err.println("\t[-t|--template]  PDF template");
			System.err.println("\t[-o|--output]    Output file name");
			System.exit(1);
		}

		if (!(new File(dataFile).exists())) {
			System.err.println("Could not open input data file " + dataFile);
			System.exit(1);
		}

		if (!(new File(templateFile).exists())) {
			System.err.println("Could not open template file " + templateFile);
			System.exit(1);
		}

		Serializer serializer = new Persister();
		FormElementList elementList = serializer.read(FormElementList.class,
				new File(dataFile));

		PdfReader templateReader = new PdfReader(templateFile);
		PdfStamper stamper = new PdfStamper(templateReader,
				new FileOutputStream(outputFile));

		// Iterate
		fillForm(stamper.getAcroFields(), elementList);

		stamper.close();
		System.exit(0);
	}

	/**
	 * Populate an existing PDF form with <FormElement> values in a
	 * <FormElementList>
	 * 
	 * @param form
	 * @param elementList
	 * @throws IOException
	 * @throws DocumentException
	 */
	public static void fillForm(AcroFields form, FormElementList elementList)
			throws IOException, DocumentException {
		for (FormElement e : elementList.getElements()) {
			try {
				form.setField(e.getName(), e.getValue());
			} catch (DocumentException ex) {
				System.out.println("Error caught: " + ex.toString());
			}
		}
	}

	/**
	 * Simple static function to build CLI options.
	 * 
	 * @return
	 */
	public static Options getOptions() {
		Options o = new Options();
		o.addOption("i", "input", true, "XML input data file");
		o.addOption("o", "output", true, "PDF output file");
		o.addOption("t", "template", true, "PDF template file");
		return o;
	}

}
