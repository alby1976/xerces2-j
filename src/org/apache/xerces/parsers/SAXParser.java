/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.xerces.parsers;

import java.io.IOException;

import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.XMLDocumentScanner;
import org.apache.xerces.impl.XMLDTDScanner;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLInputSource;
import org.apache.xerces.impl.XMLValidator;
import org.apache.xerces.impl.validation.DatatypeValidatorFactory;
import org.apache.xerces.impl.validation.GrammarPool;
import org.apache.xerces.impl.validation.datatypes.DatatypeValidatorFactoryImpl;

import org.apache.xerces.util.SymbolTable;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * This is the main Xerces SAX parser class. It uses the abstract SAX parser
 * with a document scanner, a dtd scanner, and a validator, as well as a
 * grammar pool.
 *
 * @author Stubs generated by DesignDoc on Mon Sep 11 11:10:57 PDT 2000
 * @author Arnaud  Le Hors, IBM
 * @author Andy Clark, IBM
 *
 * @version $Id$ */
public class SAXParser
    extends AbstractSAXParser {

    //
    // Constants
    //

    // debugging

    /** Set to true and recompile to print exception stack trace. */
    private static final boolean PRINT_EXCEPTION_STACK_TRACE = false;

    //
    // Data
    //

    // components (non-configurable)

    /** Grammar pool. */
    protected GrammarPool fGrammarPool;

    /** Datatype validator factory. */
    protected DatatypeValidatorFactory fDatatypeValidatorFactory;

    // components (configurable)

    /** Document scanner. */
    protected XMLDocumentScanner fScanner;

    /** DTD scanner. */
    protected XMLDTDScanner fDTDScanner;

    /** Validator. */
    protected XMLValidator fValidator;

    //
    // Constructors
    //

    /**
     * Constructs a document parser using the default symbol table
     * and grammar pool.
     */
    public SAXParser() {
        this(new SymbolTable(), new GrammarPool());
    } // <init>()

    /** 
     * Constructs a document parser using the specified symbol table
     * and a default grammar pool.
     */
    public SAXParser(SymbolTable symbolTable) {
        this(symbolTable, new GrammarPool());
    } // <init>(SymbolTable)

    /**
     * Constructor allowing to specify the symbol table and grammar pool 
     * to use. The symbol table and grammar pool are specified together
     * because the grammars contained in the grammar pool must use the
     * symbols from the specified symbol table.
     * 
     * @param symbolTable The symbol table.
     * @param grammarPool The grammar pool.
     */
    public SAXParser(SymbolTable symbolTable, GrammarPool grammarPool) {
        super(symbolTable);

        // set default features
        final String NAMESPACES = Constants.SAX_FEATURE_PREFIX + Constants.NAMESPACES_FEATURE;
        fFeatures.put(NAMESPACES, Boolean.TRUE);
        final String VALIDATION = Constants.SAX_FEATURE_PREFIX + Constants.VALIDATION_FEATURE;
        fFeatures.put(VALIDATION, Boolean.FALSE);
        final String EXTERNAL_GENERAL_ENTITIES = Constants.SAX_FEATURE_PREFIX + Constants.EXTERNAL_GENERAL_ENTITIES_FEATURE;
        fFeatures.put(EXTERNAL_GENERAL_ENTITIES, Boolean.TRUE);
        final String EXTERNAL_PARAMETER_ENTITIES = Constants.SAX_FEATURE_PREFIX + Constants.EXTERNAL_PARAMETER_ENTITIES_FEATURE;
        fFeatures.put(EXTERNAL_PARAMETER_ENTITIES, Boolean.TRUE);

        // create and register components
        final String SYMBOL_TABLE = Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;
        fProperties.put(SYMBOL_TABLE, fSymbolTable);

        fGrammarPool = grammarPool;
        final String GRAMMAR_POOL = Constants.XERCES_PROPERTY_PREFIX + Constants.GRAMMAR_POOL_PROPERTY;
        fProperties.put(GRAMMAR_POOL, fGrammarPool);

        fScanner = createDocumentScanner();
        final String DOCUMENT_SCANNER = Constants.XERCES_PROPERTY_PREFIX + Constants.DOCUMENT_SCANNER_PROPERTY;
        fProperties.put(DOCUMENT_SCANNER, fScanner);

        fDTDScanner = createDTDScanner();
        final String DTD_SCANNER = Constants.XERCES_PROPERTY_PREFIX + Constants.DTD_SCANNER_PROPERTY;
        fProperties.put(DTD_SCANNER, fDTDScanner);

        fValidator = createValidator();
        final String VALIDATOR = Constants.XERCES_PROPERTY_PREFIX + Constants.VALIDATOR_PROPERTY;
        fProperties.put(VALIDATOR, fValidator);
        
        fDatatypeValidatorFactory = createDatatypeValidatorFactory();
        final String DATATYPE_VALIDATOR_FACTORY = Constants.XERCES_PROPERTY_PREFIX + Constants.DATATYPE_VALIDATOR_FACTORY_PROPERTY;
        fProperties.put(DATATYPE_VALIDATOR_FACTORY, fDatatypeValidatorFactory);

    } // <init>(SymbolTable,GrammarPool)

    //
    // XMLParser methods
    //

    /** 
     * Reset all components before parsing. 
     *
     * @throws SAXException Thrown if an error occurs during initialization.
     */
    protected void reset() throws SAXException {
        super.reset();


        // setup document pipeline
        fScanner.setDocumentHandler(fValidator);
        fValidator.setDocumentHandler(this);

        // setup dtd pipeline
        fDTDScanner.setDTDHandler(fValidator);
        fValidator.setDTDHandler(this);

        // setup dtd content model pipeline
        /***/
        fDTDScanner.setDTDContentModelHandler(fValidator);
        fValidator.setDTDContentModelHandler(this);

        // reset every component
        fScanner.reset(this);
        fDTDScanner.reset(this);
        fValidator.reset(this);

    } // reset()

    //
    // XMLReader methods
    //

    /**
     * Set the state of a feature.
     * <p>
     * The feature name is any fully-qualified URI.  It is
     * possible for an XMLReader to recognize a feature name but
     * to be unable to set its value; this is especially true
     * in the case of an adapter for a SAX1 {@link org.xml.sax.Parser Parser},
     * which has no way of affecting whether the underlying parser is
     * validating, for example.
     * <p>
     * Some feature values may be immutable or mutable only 
     * in specific contexts, such as before, during, or after 
     * a parse.
     *
     * @param name The feature name, which is a fully-qualified URI.
     * @param state The requested state of the feature (true or false).
     *
     * @exception org.xml.sax.SAXNotRecognizedException When the
     *            XMLReader does not recognize the feature name.
     * @exception org.xml.sax.SAXNotSupportedException When the
     *            XMLReader recognizes the feature name but 
     *            cannot set the requested value.
     *
     * @see #getFeature
     */
    public void setFeature(String featureId, boolean state)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        super.setFeature(featureId, state);

        // forward to every component
        fScanner.setFeature(featureId, state);
        fDTDScanner.setFeature(featureId, state);
        fValidator.setFeature(featureId, state);

    } // setFeature(String,boolean)

    /**
     * Set the value of a property.
     * <p>
     * The property name is any fully-qualified URI.  It is
     * possible for an XMLReader to recognize a property name but
     * to be unable to set its value; this is especially true
     * in the case of an adapter for a SAX1 {@link org.xml.sax.Parser
     * Parser}.
     * <p>
     * Some property values may be immutable or mutable only 
     * in specific contexts, such as before, during, or after 
     * a parse.
     * <p>
     * This method is also the standard mechanism for setting
     * extended handlers.
     *
     * @param name The property name, which is a fully-qualified URI.
     * @param state The requested value for the property.
     *
     * @exception org.xml.sax.SAXNotRecognizedException When the
     *            XMLReader does not recognize the property name.
     * @exception org.xml.sax.SAXNotSupportedException When the
     *            XMLReader recognizes the property name but 
     *            cannot set the requested value.
     *
     * @see #getProperty
     */
    public void setProperty(String propertyId, Object value)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        super.setProperty(propertyId, value);

        // forward to every component
        fScanner.setProperty(propertyId, value);
        fDTDScanner.setProperty(propertyId, value);
        fValidator.setProperty(propertyId, value);

    } // setProperty(String,Object)

    /**
     * Parses the specified input source.
     *
     * @param source The input source.
     *
     * @exception org.xml.sax.SAXException Throws exception on SAX error.
     * @exception java.io.IOException Throws exception on i/o error.
     */
    public void parse(InputSource source)
        throws SAXException, IOException {

        if (fParseInProgress) {
            // REVISIT - need to add new error message
            throw new SAXException("FWK005 parse may not be called while parsing.");
        }

        try {
            reset();
            fEntityManager.setEntityHandler(fScanner);
            fEntityManager.startDocumentEntity(new XMLInputSource(source));
            fScanner.scanDocument(true);
            fParseInProgress = false;
        } 
        catch (SAXException ex) {
            fParseInProgress = false;
            if (PRINT_EXCEPTION_STACK_TRACE)
                ex.printStackTrace();
            throw ex;
        } 
        catch (IOException ex) {
            fParseInProgress = false;
            if (PRINT_EXCEPTION_STACK_TRACE)
                ex.printStackTrace();
            throw ex;
        } 
        catch (Exception ex) {
            fParseInProgress = false;
            if (PRINT_EXCEPTION_STACK_TRACE)
                ex.printStackTrace();
            throw new org.xml.sax.SAXException(ex);
        }

    } // parse(InputSource)

    //
    // XMLParser methods
    //

    /**
     * Check a feature. If feature is know and supported, this method simply
     * returns. Otherwise, the appropriate exception is thrown.
     *
     * @param featureId The unique identifier (URI) of the feature.
     *
     * @exception org.xml.sax.SAXNotRecognizedException If the
     *            requested feature is not known.
     * @exception org.xml.sax.SAXNotSupportedException If the
     *            requested feature is known, but the requested
     *            state is not supported.
     * @exception org.xml.sax.SAXException If there is any other
     *            problem fulfilling the request.
     */
    protected void checkFeature(String featureId)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        //
        // Xerces Features
        //

        if (featureId.startsWith(Constants.XERCES_FEATURE_PREFIX)) {
            String feature = featureId.substring(Constants.XERCES_FEATURE_PREFIX.length());
            //
            // http://apache.org/xml/features/validation/schema
            //   Lets the user turn Schema validation support on/off.
            //
            if (feature.equals(Constants.SCHEMA_VALIDATION_FEATURE)) {
                return;
            }
            //
            // http://apache.org/xml/features/validation/dynamic
            //   Allows the parser to validate a document only when it
            //   contains a grammar. Validation is turned on/off based
            //   on each document instance, automatically.
            //
            if (feature.equals(Constants.DYNAMIC_VALIDATION_FEATURE)) {
                return;
            }
            //
            // http://apache.org/xml/features/validation/default-attribute-values
            //
            if (feature.equals(Constants.DEFAULT_ATTRIBUTE_VALUES_FEATURE)) {
                // REVISIT
                throw new SAXNotSupportedException(featureId);
            }
            //
            // http://apache.org/xml/features/validation/default-attribute-values
            //
            if (feature.equals(Constants.VALIDATE_CONTENT_MODELS_FEATURE)) {
                // REVISIT
                throw new SAXNotSupportedException(featureId);
            }
            //
            // http://apache.org/xml/features/validation/nonvalidating/load-dtd-grammar
            //
            if (feature.equals(Constants.LOAD_DTD_GRAMMAR_FEATURE)) {
                return;
            }
            //
            // http://apache.org/xml/features/validation/nonvalidating/load-external-dtd
            //
            if (feature.equals(Constants.LOAD_EXTERNAL_DTD_FEATURE)) {
                return;
            }

            //
            // http://apache.org/xml/features/validation/default-attribute-values
            //
            if (feature.equals(Constants.VALIDATE_DATATYPES_FEATURE)) {
                throw new SAXNotSupportedException(featureId);
            }
        }

        //
        // Not recognized
        //

        super.checkFeature(featureId);

    } // checkFeature(String)

    /**
     * Check a property. If the property is know and supported, this method
     * simply returns. Otherwise, the appropriate exception is thrown.
     *
     * @param propertyId The unique identifier (URI) of the property
     *                   being set.
     * @exception org.xml.sax.SAXNotRecognizedException If the
     *            requested property is not known.
     * @exception org.xml.sax.SAXNotSupportedException If the
     *            requested property is known, but the requested
     *            value is not supported.
     * @exception org.xml.sax.SAXException If there is any other
     *            problem fulfilling the request.
     */
    protected void checkProperty(String propertyId)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        //
        // Xerces Properties
        //

        if (propertyId.startsWith(Constants.XERCES_PROPERTY_PREFIX)) {
            String property = propertyId.substring(Constants.XERCES_PROPERTY_PREFIX.length());
            if (property.equals(Constants.DTD_SCANNER_PROPERTY)) {
                return;
            }
        }

        //
        // Not recognized
        //

        super.checkProperty(propertyId);

    } // checkProperty(String)

    //
    // Protected methods
    //

    // factory methods

    /** Create a document scanner. */
    protected XMLDocumentScanner createDocumentScanner() {
        return new XMLDocumentScanner();
    } // createDocumentScanner():XMLDocumentScanner

    /** Create a DTD scanner. */
    protected XMLDTDScanner createDTDScanner() {
        return new XMLDTDScanner();
    } // createDTDScanner():XMLDTDScanner

    /** Create a validator. */
    protected XMLValidator createValidator() {
        return new XMLValidator();
    } // createValidator():XMLValidator

    /** Create a datatype validator factory. */
    protected DatatypeValidatorFactory createDatatypeValidatorFactory() {
        return new DatatypeValidatorFactoryImpl();
    } // createDatatypeValidatorFactory():DatatypeValidatorFactory

} // class SAXParser
