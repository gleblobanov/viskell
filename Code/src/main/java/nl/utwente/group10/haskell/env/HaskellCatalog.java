package nl.utwente.group10.haskell.env;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import nl.utwente.group10.haskell.exceptions.CatalogException;
import nl.utwente.group10.haskell.type.Type;
import nl.utwente.group10.haskell.type.TypeClass;
import nl.utwente.group10.haskell.type.TypeCon;
import nl.utwente.group10.haskell.typeparser.TypeBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.SchemaFactory;

/**
 * Haskell catalog containing available type classes and functions.
 */
public class HaskellCatalog {
    private Map<String, TypeClass> classes;

    private Map<String, FunctionEntry> functions;

    private Multimap<String, FunctionEntry> categories;

    /** Default path to the XML file. */
    public static final String XML_PATH = "/catalog/catalog.xml";

    /** Default path to the XSD file. */
    public static final String XSD_PATH = "/catalog/catalog.xsd";

    /**
     * Constructs a Haskell catalog using the given file location.
     * @param path The path to the catalog XML file.
     * @throws CatalogException
     */
    public HaskellCatalog(final String path) throws CatalogException {
        this.functions = new HashMap<>();
        this.categories = HashMultimap.create();

        Document doc = getDocument(path, HaskellCatalog.XSD_PATH);

        NodeList classNodes = doc.getElementsByTagName("class");
        NodeList functionNodes = doc.getElementsByTagName("function");

        this.classes = this.parseClasses(classNodes);

        Set<FunctionEntry> entries = this.parseFunctions(functionNodes, this.classes);
      
        for (FunctionEntry entry : entries) {
            this.functions.put(entry.getName(), entry);
            this.categories.put(entry.getCategory(), entry);
        }
    }

    /**
     * Constructs a Haskell catalog using the default file location.
     * @throws CatalogException
     */
    public HaskellCatalog() throws CatalogException {
        this(HaskellCatalog.XML_PATH);
    }

    /**
     * @return The set of category names.
     */
    public final Set<String> getCategories() {
        return this.categories.keySet();
    }

    /**
     * @param key The name of the category.
     * @return A set of the entries in the given category.
     */
    public final Collection<FunctionEntry> getCategory(final String key) {
        return this.categories.get(key);
    }

    /**
     * @return A new environment based on the entries of this catalog.
     */
    public final Environment asEnvironment() {
        Map<String, Type> functions = new HashMap<>();

        // Build function type map
        for (FunctionEntry entry : this.functions.values()) {
            functions.put(entry.getName(), entry.getSignature());
        }

        return new Environment(functions, new HashMap<String, TypeClass>(this.classes));
    }

    /**
     * Parses a list of class nodes into ClassEntry objects.
     * @param nodes The nodes to parse.
     * @return A set of ClassEntry objects for the given nodes.
     * @throws CatalogException
     */
    protected final Map<String, TypeClass> parseClasses(NodeList nodes) throws CatalogException {
        Map<String, TypeClass> entries = new HashMap<>();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            String name = node.getAttributes().getNamedItem("name").getTextContent();
            TypeClass tc = new TypeClass(name);
            TypeBuilder builder = new TypeBuilder(entries);
            
            NodeList inodes = node.getChildNodes();
            for (int j = 0; j < inodes.getLength(); j++) {
                Node inode = inodes.item(j);
                NamedNodeMap attrs = inode.getAttributes();
                String inst = attrs.getNamedItem("name").getTextContent();
                if ("instance".equals(inode.getNodeName())) {
                    Type t = builder.build(inst);
                    if (t instanceof TypeCon) {
                        tc.addType((TypeCon) t);
                    }
                } else if ("superClass".equals(inode.getNodeName())) {
                    TypeClass sc = entries.get(inst);
                    if (sc == null) {
                       throw new CatalogException("Can't resolve superclass " + inst + " of " + name);
                    } else {
                        tc.addSuperClass(sc);
                    }
                }
            }
            
            entries.put(name,tc);
        }

        return entries;
    }
    
    /**
     * Parses a list of function nodes into FunctionEntry objects.
     * @param nodes The nodes to parse.
     * @return A set of FunctionEntry objects for the given nodes.
     */
    protected final Set<FunctionEntry> parseFunctions(NodeList nodes, Map<String, TypeClass> typeClasses) {
        Set<FunctionEntry> entries = new HashSet<>();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            NamedNodeMap attributes = node.getAttributes();

            String name = attributes.getNamedItem("name").getTextContent();
            String signature = attributes.getNamedItem("signature").getTextContent();
            String category = node.getParentNode().getAttributes().getNamedItem("name").getTextContent();
            String documentation = node.getTextContent();

            TypeBuilder builder = new TypeBuilder(typeClasses);
            Type tsig = builder.build(signature);
            entries.add(new FunctionEntry(name, category, tsig, documentation));
        }

        return entries;
    }

    /**
     * Loads the given XML catalog into a document.
     * @param XMLPath The path to the XML file.
     * @param XSDPath The path to the XSD file.
     * @return The document for the XML file.
     * @throws CatalogException
     */
    protected static Document getDocument(final String XMLPath, final String XSDPath) throws CatalogException {
        URL xmlFile = HaskellCatalog.class.getResource(XMLPath);
        URL schemaFile = HaskellCatalog.class.getResource(XSDPath);

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            SchemaFactory sFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

            dbFactory.setIgnoringElementContentWhitespace(true);
            dbFactory.setIgnoringComments(true);
            dbFactory.setSchema(sFactory.newSchema(schemaFile));

            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            return dBuilder.parse(xmlFile.openStream());
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new CatalogException(e);
        }
    }
    
}
