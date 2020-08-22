package validation;



import dao.JpaDao;
import helper.HelperFuncs;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;

public class Controller {

    @FXML
    public Button multiplyButton;

    @FXML
    public TextField resultTextField;

    @FXML
    public TextField firstTextField;

    @FXML
    public TextField secTextField;

    @FXML
    public TextArea responseMessage;

    @FXML
    public Pane mainPane;

    @FXML
    public TabPane tabPane;

    @FXML
    public Button createButton;

    private static String PKGNAME = "tables";

    private List<String> tableNames = new ArrayList<>();
    private Map<String, List<String>> tableToFields = new HashMap<>();
    private Map<String, String> tableToPrimaryKey = new HashMap<>();
    private List<TableView> tableViewsList = new ArrayList<>();
    private List<JpaDao> daoList;
    // initially 0-th tab
    private static int SELECTED_TAB_INDEX = 0;


    @FXML
    public void initialize() throws IOException, InterruptedException, ParserConfigurationException, SAXException, ClassNotFoundException {
        String path = System.getProperties().getProperty("user.dir").concat("\\src\\main\\resources\\table_structure.xml");
        File file = new File(path);
        setTabPaneListener();
        Element element = getElement(file);
        parseElement(element);

        System.out.println(tableToPrimaryKey);
        printTableToFieldsMap();
        createTabs();
        createTableViews();
    }

    public Element getElement(File file) throws ParserConfigurationException, IOException, SAXException {
        //Get Document Builder
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        //Build document
        Document document = docBuilder.parse(file);
        // Normalize XML Structure (purpose???)
        document.getDocumentElement().normalize();
        Element element = document.getDocumentElement();
        return element;
    }

    public void parseElement(Element element) {
        if (element.hasChildNodes()) {
            NodeList nodeList = element.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String tableName = node.getAttributes().getNamedItem("name").getNodeValue();
                    tableNames.add(tableName);
                    if (node.hasChildNodes()) {
                        parseNodeChildren(node, tableName, "tables");
                    }
                }
            }
        }
    }

    public void parseNodeChildren(Node node, String tableName, String pkgName) {
        NodeList children = node.getChildNodes();
        List<String> columns = new ArrayList<>();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String columnName = child.getAttributes().getNamedItem("name").getNodeValue();
                String isPK = child.getAttributes().getNamedItem("IsPrimaryKey").getNodeValue();
                if (isPK.equals("1")) {
                    System.out.println("This is the PK column of table " + tableName + ": " + columnName);
                    tableToPrimaryKey.put(tableName, columnName);
                }
                columns.add(columnName);
            }
        }
        tableName =  pkgName.concat(".").concat(HelperFuncs.toCamel(tableName));
        tableToFields.put(tableName, columns);
    }

    public List<String> convertTableNames(List<String> names, String pkgName) {
        List<String> results = new ArrayList<String>();
        for (String name : names) {
            name = pkgName.concat(".").concat(HelperFuncs.toCamel(name));
            results.add(name);
        }

        for (String s : results)
            System.out.println(s);

        return results;
    }

    public List<JpaDao> makeJPAList() throws ClassNotFoundException {
        Set<String> tables = tableToFields.keySet();
        //System.out.println("Tables: \n" + tables);
        List<String> tablesSorted = new ArrayList<>(tables);
        Collections.sort(tablesSorted);
        daoList = new ArrayList<JpaDao>();
        for (String table : tablesSorted) {
            Class classObject = Class.forName(table);
            JpaDao dao = new JpaDao(classObject);
            daoList.add(dao);
        }
        return daoList;
    }

    public void createTabs() {
        for (String name : tableNames) {
            Tab tab = new Tab(name);
            tabPane.getTabs().add(tab);
        }
    }

    public void createTableViews() throws ClassNotFoundException {
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            String tabNameToMapKey = PKGNAME.concat(".").concat(HelperFuncs.toCamel(tabPane.getTabs().get(i).getText()));
            TableView tableView = new TableView();
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableViewsList.add(tableView);
            tableView.setEditable(true);
            setRowListener(tableView);

            List<String> columnNames = tableToFields.get(tabNameToMapKey);
            System.out.println("Tab: " + tabPane.getTabs().get(i).getText());
            System.out.println("Columns: " + columnNames);

            for (String name : columnNames) {
                TableColumn tableColumn = getTableColumn(name);
                tableView.getColumns().add(tableColumn);
            }
            HBox hBox = new HBox(createButton);
            hBox.setAlignment(Pos.TOP_RIGHT);
            mainPane.getChildren().add(hBox);
            createButton.setOnAction(actionEvent -> {
                System.out.println(daoList.get(SELECTED_TAB_INDEX).getType().getName());
                try {
                    Class object = Class.forName(daoList.get(SELECTED_TAB_INDEX).getType().getName());
                    Object newRecord = object.getDeclaredConstructor().newInstance();
                    daoList.get(SELECTED_TAB_INDEX).create((Serializable)newRecord);
                    refreshTable(tabPane.getSelectionModel().getSelectedItem(),tableViewsList.get(SELECTED_TAB_INDEX));
                } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
            populateTableView(tabPane.getTabs().get(i), tableView);
            tabPane.getTabs().get(i).setContent(tableView);

        }

    }

    private void setRowListener(TableView tableView){

        tableView.setRowFactory(tableView1 -> {
            final TableRow row = new TableRow();
            final ContextMenu rowMenu = new ContextMenu();
            MenuItem removeItem = new MenuItem("Delete");
            removeItem.setOnAction(actionEvent -> {
                Class recordsType = daoList.get(SELECTED_TAB_INDEX).getType();
                Object currentItem =((TableView) tabPane.getSelectionModel().getSelectedItem().getContent()).getSelectionModel().getSelectedItem();
                try {
                    String tableName = tabPane.getSelectionModel().getSelectedItem().getText();
                    String pkColumn = tableToPrimaryKey.get(tableName);
                    String formatted = pkColumn.substring(0, 1).toUpperCase() + pkColumn.substring(1);
                    Method method = recordsType.getMethod("get" + formatted);
                    int id = (int) method.invoke(currentItem);
                    daoList.get(SELECTED_TAB_INDEX).deleteById(id);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                try {
                    refreshTable(tabPane.getSelectionModel().getSelectedItem(),tableViewsList.get(SELECTED_TAB_INDEX));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
            rowMenu.getItems().addAll(removeItem);
            row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty()))
                    .then(rowMenu)
                    .otherwise((ContextMenu)null));
            return row;
        });

    }

    private void refreshTable(Tab tab, TableView tableView) throws ClassNotFoundException {
        populateTableView(tab,tableView);
        tab.setContent(tableView);
    }

    private TableColumn getTableColumn(String name) {
        TableColumn tableColumn = new TableColumn(name);
        tableColumn.setCellValueFactory(new PropertyValueFactory(name));
        tableColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Object>() {
            @Override
            public String toString(Object o) {
                return o.toString();
            }

            @Override
            public Object fromString(String s) {
                return s.toString();
            }
        }));
        tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent cellEditEvent) {
                String updatedValue = (String) cellEditEvent.getNewValue();
                String old = String.valueOf(cellEditEvent.getOldValue());
                System.out.println("Old: " + old + "\nNew: " + updatedValue);
                System.out.println("Column Name: " + cellEditEvent.getTableColumn().getText());
                String column = cellEditEvent.getTableColumn().getText();
                String upperFirst = column.substring(0,1).toUpperCase().concat(column.substring(1));
                System.out.println("Edited record index: " + ((TableView) tabPane.getSelectionModel().getSelectedItem().getContent()).getSelectionModel().getSelectedIndex());
                Object recordToUpdate = ((TableView)tabPane.getSelectionModel().getSelectedItem().getContent()).getSelectionModel().getSelectedItem();
                Class recordsType = daoList.get(SELECTED_TAB_INDEX).getType();
                try {
                    Method method = recordsType.getMethod("set".concat(upperFirst), String.class);
                    System.out.println(method.getName());
                    method.invoke(recordToUpdate, updatedValue);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                //print resulting table
                System.out.println("Printing resulting records list:\n " + ((TableView)  tabPane.getSelectionModel().getSelectedItem().getContent()).getItems());
                daoList.get(SELECTED_TAB_INDEX).update((Serializable) recordToUpdate);
            }
        });
        return tableColumn;
    }
    public void setTabPaneListener() {
        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observableValue, Tab tab, Tab t1) {
                if (tab != null)
                    System.out.println("Old tab: " + tab.getText());

                System.out.println("New tab: " + t1.getText());
                SELECTED_TAB_INDEX = tabPane.getSelectionModel().getSelectedIndex();
                System.out.println("Selected tab index: " + SELECTED_TAB_INDEX);
            }
        });
    }

    public void populateTableView(Tab currentTab, TableView tableView) throws ClassNotFoundException {
        List<JpaDao> daoList = makeJPAList();
        int jpaIndex = tabPane.getTabs().indexOf(currentTab);
        JpaDao dao = daoList.get(jpaIndex);
        ObservableList list = FXCollections.observableArrayList(dao.findAll());
        tableView.setItems(list);
    }

    public void printTableToFieldsMap() {
        Set<Map.Entry<String, List<String>>> set = tableToFields.entrySet();
        for (Map.Entry<String, List<String>> entry : set) {
            System.out.println("Table Name: " + entry.getKey());
            System.out.println("Fields: " + entry.getValue());
        }
    }

    public String fieldNameToMethodName(String fieldName) {
        String result = fieldName.toLowerCase();
        result = result.replaceFirst(String.valueOf(result.charAt(0)), String.valueOf(Character.toUpperCase(result.charAt(0))));
        return "get".concat(result);
    }

}
