package radar;

import org.apache.http.HttpEntity;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import radar.item.Cluster;
import radar.item.Items;

import java.io.*;
import java.util.*;

public class ItemIO {

    private Map<String, Map<String, String>> anonymizedValues = new HashMap<>();
    private List<String> statusOrder = new ArrayList<>();
    private static final String jiraHost = "http://jira.novatec-gmbh.de";
    private String username;
    private String password;

    private List<Map<String, Object>> fromJira;

    {
        // "reduce", "work", "build-up", "evaluate", "observe"
        statusOrder.add("observe");
        statusOrder.add("evaluate");
        statusOrder.add("build-up");
        statusOrder.add("work");
        statusOrder.add("reduce");
    }

    public void setUsernamePassword (String un, String pw) {
        this.username = un;
        this.password = pw;
    }

    public void initFromFilename(String filename, boolean includeDeletedAndDups) {
        try {
            initFromInputStream(new FileInputStream(filename), includeDeletedAndDups);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void initFromInputStream(InputStream is, boolean includeDeletedAndDups) {
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            List<Map<String, Object>> l = readItems(br, includeDeletedAndDups);
            initItems(l);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean initFromLastJiraRequest(boolean TRStatusRestriction) {
        try {
            File responseFile = new File("Jira-Response-TechRadar-" + TRStatusRestriction + ".json");
            if (! responseFile.exists()) {
                return false;
            }
            BufferedReader br = new BufferedReader(new FileReader(responseFile));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            br.close();
            JSONObject response = new JSONObject(sb.toString());

            initFromJiraResponse(response);

            System.out.println("loaded from last Jira response");
            return true;
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        return false;
    }

    public void initFromJira(boolean TRStatusOnly) {
        JSONObject response = new JSONObject();
        try {
            //unused   unused  summary  description   Topic   status   customfield_13501  "Strategic Topic"
            String statusRestriction;
            if (TRStatusOnly) {
                statusRestriction = "+and+status+in+(Observe,Evaluate,Build-Up,Work,Reduce)";
            } else{
                statusRestriction = "";
            }
            response = get("/rest/api/2/search?" +
                    "jql=project=NTTR" + statusRestriction + "&maxResults=5000" +
                    "&fields=key,summary,customfield_13513,status,customfield_13501,customfield_13502,customfield_13503,assignee");

            initFromJiraResponse(response);

            File responseFile = new File("Jira-Response-TechRadar-" + TRStatusOnly + ".json");
            System.out.println("Writing Jira Response to " + responseFile.getAbsolutePath());
            BufferedWriter bw = new BufferedWriter(new FileWriter(responseFile));
            bw.write(response.toString());
            bw.close();
        } catch (Exception e) {
            System.out.println("response:" + response.toString());
            e.printStackTrace(System.out);
        }
    }

    public void initFromJiraResponse(JSONObject response) {
        try {
            JSONArray issues = response.getJSONArray("issues");
            List<Map<String, Object>> l = readItems (issues, true);
            fromJira = new ArrayList<>(l);
            initItems(l);
        } catch (Exception e) {
            System.out.println("response:" + response.toString());
            e.printStackTrace(System.out);
        }
    }

    private synchronized  List<Map<String, Object>> readItems(JSONArray issues, boolean initDefaults) {
        // {"expand":"operations,versionedRepresentations,editmeta,changelog,renderedFields",
        // "self":"https://jira.novatec-gmbh.de/rest/api/2/issue/134300","id":"134300",
        // "fields":{
        // "summary":"Scala Lang",
        // "description":null,
        // "customfield_13501":{"self":"https://jira.novatec-gmbh.de/rest/api/2/customFieldOption/13299","id":"13299","value":"Languages"},
        // "status":{"name":"Open","self":"https://jira.novatec-gmbh.de/rest/api/2/status/1",
        //      "description":"The issue is open and ready for the assignee to start work on it.",
        //      "iconUrl":"https://jira.novatec-gmbh.de/images/icons/statuses/open.png","id":"1",
        //      "statusCategory":{"colorName":"blue-gray",
        //      "name":"To Do","self":"https://jira.novatec-gmbh.de/rest/api/2/statuscategory/2",
        //      "id":2,"key":"new"}}},
        // "key":"NTTR-508"}

        /*
        for (int i = 0; i < issues.length(); i++) {
            JSONObject issue = (JSONObject) issues.get(i);
            JSONObject fields = (JSONObject) issue.get("fields");
            System.out.println("issue[" + issue.get("key") + ", " + fields.get("summary"));
        }
        */



        List<Map<String, Object>> items = new ArrayList<>();
        Cluster.setAnonymize(false);
        JSONObject issue = null;
        try {

           for (int i = 0; i < issues.length(); i++) {
                issue = (JSONObject) issues.get(i);
                System.out.println(issue);
                // Deleted	Dup	Name	Description	Topic	Ring	Category	Strategic Topic

                //int ring = -1;

                //String deleted = ""; //getValue("Deleted", "", p, headers).trim();
                //String dup = ""; //getValue("Dup", "", p, headers);

                //if (deleted.length() == 0 && dup.length() == 0)
                {
                    Map<String, Object> item = new HashMap<>();
                    items.add(item);
                    Map<Cluster, String> values = new HashMap<>();
                    if (initDefaults) {
                        values.put(Cluster.STRATEGIC_TOPIC, "undefined");
                        values.put(Cluster.TOPIC, "undefined");
                        values.put(Cluster.CATEGORY, "undefined");
                        item.put(Cluster.CATEGORY.getColumn(), "undefined");
                    }
                    JSONObject fields = issue.getJSONObject("fields");
                    item.put("id", issue.getString("key"));
                    String ringText = fields.getJSONObject("status").getString("name");
                    item.put("Ring", ringText);
                   /* if (ringText.equals("Open")) {
                        System.out.println("open:" + issue.toString());
                    }*/
                    String category = null;
                    if (fields.has("customfield_13501") && fields.get("customfield_13501") instanceof JSONObject) {
                        category = fields.getJSONObject("customfield_13501").getString("value");
                    }
                    if (category == null || category.trim().length() == 0) {
                        category = "undefined";
                    }
                    item.put(Cluster.CATEGORY.getColumn(), category);
                    if (fields.has("assignee") && fields.get("assignee") instanceof JSONObject) {
                        item.put("Assignee", fields.getJSONObject("assignee").getString("name"));
                    }
                    //if (category !=  DesktopDemo.PLACEHOLDER)
                    {
                        item.put("Name", fields.getString("summary"));
                        Object desc = fields.get("customfield_13513");
                        if (desc instanceof String) {
                            item.put("Description", desc);
                        }
                        item.put("Size (0-4)", "2");
                        item.put("Percentage", "100");
                        if (fields.has("assignee") && fields.get("assignee") instanceof JSONObject) {
                            item.put("assignee", fields.getJSONObject("assignee").getString("name"));
                        }
                        if (fields.has("customfield_13502") && fields.get("customfield_13502") instanceof JSONObject) {
                            values.put(Cluster.STRATEGIC_TOPIC, fields.getJSONObject("customfield_13502").getString("value"));
                        }
                        values.put (Cluster.CATEGORY, category);
                        if (fields.has("customfield_13503") && fields.get("customfield_13503") instanceof JSONObject) {
                            values.put(Cluster.TOPIC, fields.getJSONObject("customfield_13503").getString("value"));
                        }
                        if (Config.anonymized) {
                            item.put ("Name", getAnonymizedValue("Itemname", (String) item.get("Name"), "item"));
                            Map<Cluster, String> values2 = new HashMap<>();
                            for (Map.Entry<Cluster, String> e : values.entrySet()) {
                                values2.put(e.getKey(), getAnonymizedValue(e.getKey().getRawColumn(), e.getValue(), "group"));
                            }
                            values = values2;
                        }
                        item.put("Values", values);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(issue.toString());
            e.printStackTrace(System.out);
            System.exit(0);
        }
        Cluster.setAnonymize(Config.anonymized);
        return items;
    }


    private synchronized  List<Map<String, Object>> readItems(BufferedReader r, boolean includeDeletedAndDups) {
        List<Map<String, Object>> items = new ArrayList<>();
        String line = null;
        Cluster.setAnonymize(false);
        try {
            line = r.readLine();
            String[] headers = line.split("\t");

            while ((line = r.readLine()) != null) {
                System.out.println(line);
                // Deleted	Dup	Name	Description	Topic	Ring	Category	Strategic Topic

                String[] p = line.split("\t");
                int ring = -1;

                String deleted = getValue("Deleted", "", p, headers).trim();
                String dup = getValue("Dup", "", p, headers);

                if (includeDeletedAndDups || deleted.length() == 0 && dup.length() == 0) {
                    Map<String, Object> item = new HashMap<>();
                    if (includeDeletedAndDups) {
                        item.put("Deleted", deleted);
                        item.put("Dup", dup);
                    }
                    items.add(item);
                    String ringText = getValue("Ring", null, p, headers);
                    item.put("Ring", ringText);
                    String category = getValue(Cluster.CATEGORY.getColumn(), "undefined", p, headers);
                    if (category != null && category.equals(DesktopDemo.PLACEHOLDER)) {
                        category = DesktopDemo.PLACEHOLDER;
                    }
                    item.put(Cluster.CATEGORY.getColumn(), category);
                    if (category !=  DesktopDemo.PLACEHOLDER) {
                        item.put("Name", getValue("Name", null, p, headers));
                        item.put("Description", getValue("Description", null, p, headers));
                        item.put("Size (0-4)", getValue("Size (0-4)", "2", p, headers));
                        item.put("Percentage", getValue("Percentage", "100", p, headers));
                        item.put("Assignee", getValue("Unknown", null, p, headers));
                        Map<Cluster, String> values = new HashMap<>();
                        for (Cluster cluster : Cluster.values()) {
                            values.put(cluster, getValue(cluster.getColumn(), null, p, headers));
                        }
                        if (Config.anonymized) {
                            item.put ("Name", getAnonymizedValue("Itemname", (String) item.get("Name"), "item"));
                            Map<Cluster, String> values2 = new HashMap<>();
                            for (Map.Entry<Cluster, String> e : values.entrySet()) {
                                values2.put(e.getKey(), getAnonymizedValue(e.getKey().getRawColumn(), e.getValue(), "group"));
                            }
                            values = values2;
                        }
                        item.put("Values", values);
                    }
                }
            }
            r.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        Cluster.setAnonymize(Config.anonymized);
        return items;
    }

    private synchronized void initItems(List<Map<String, Object>> items) {
        Items.resetTmpInstance();
        for (Map<String, Object>  me : items) {
            // Deleted	Dup	Name	Description	Topic	Ring	Category	Strategic Topic

            int ring = -1;

            String ringText = (String) me.get("Ring");
            for (int i = 0; i < Config.texts.length; i++) {
                if (ringText.toLowerCase().equals(Config.texts[i])) {
                    ring = i;
                }
            }

            if (ring >= 0) {
                Map<Cluster, String> values = (Map<Cluster, String>) me.get("Values");

                String category = (String) me.get(Cluster.CATEGORY.getColumn());
                if (category.equals(DesktopDemo.PLACEHOLDER)) {
                    Items.getTmpInstance().create(ring);
                } else {
                    String name = (String) me.get("Name");
                    String description = (String) me.get("Description");
                    int groesse = Integer.parseInt((String) me.get("Size (0-4)"));
                    int percentage = Integer.parseInt((String) me.get("Percentage"));
                    String assignee = (String) me.get("Assignee");
                    Items.getTmpInstance().create(
                            ring,
                            name,
                            description,
                            groesse,
                            percentage,
                            values,
                            assignee
                    );
                }
            }
        }
        System.out.println("items read");
    }

    private String getValue(String name, String defaultStr, String[] tokens, String[] headers) {
        for (int i = 0; i < headers.length; i++) {
            if (
                    name.equals(headers[i])
            ) {
                if (i >= tokens.length) {
                    return defaultStr;
                } else {
                    return tokens[i];
                }
            }
        }
        return defaultStr;
    }

    private String getAnonymizedValue(String name, String value, String pfx) {
        if (! anonymizeHeader(name)) {
            return value;
        }
        Map<String, String> m = anonymizedValues.get(name);
        if (m == null) {
            m = new HashMap<>();
            anonymizedValues.put(name, m);
        }
        String anonymizedValue = m.get(value);
        if (anonymizedValue == null) {
            anonymizedValue = pfx + "-" + (m.size() + 1);
            m.put(value, anonymizedValue);
        }
        return anonymizedValue;
    }

    private boolean anonymizeHeader(String name) {
        if (
                name.equals("Größe (0-4)") ||
                        name.equals("Percentage") ||
                        name.equals("Ring")
        ) {
            return false;
        }
        return true;
    }

    public static void main2(String[] args) {

         try {
             ItemIO iio = new ItemIO();
             args = new String[]{"C:\\Work7\\New Technologies\\FutureRoadmap2\\TR Themen Survey Comparison 20190405 v3.txt"};
             if (args != null && args.length == 1) {

                 JSONObject response = iio.get("/rest/api/2/search?jql=project=NTTR&maxResults=1000" +
                         "&fields=key,summary,description,Topic,status,customfield_13501,Strategic%20Topic,assignee");
                 JSONArray issues = response.getJSONArray("issues");
                 List<Map<String, Object>> itemsJira = iio.readItems(issues, false);

                 BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));
                 List<Map<String, Object>> itemsFile = iio.readItems(br, true);
                 System.out.println(">>> read " + itemsFile.size() + " items");

                 List<Map<String, Object>> itemsDiff = new ArrayList<>();
                 for (Map<String, Object> itemFile : itemsFile) {
                     boolean found = false;
                     Map<String, Object> itemJira = null;
                     for (Map<String, Object> tmp : itemsJira) {
                         if (
                                 tmp.get("Name").equals(itemFile.get("Name"))/*  && (
                                         tmp.get("Description") == null && itemFile.get("Description") == null ||
                                                 tmp.get("Description") != null &&
                                         tmp.get("Description").equals(itemFile.get("Description"))
                                 )&&(
                                         tmp.get("Category") == null && itemFile.get("Category") == null ||
                                                 tmp.get("Category") != null &&
                                         tmp.get("Category").equals(itemFile.get("Category"))
                                 )&&(
                                         tmp.get("Ring") == null && itemFile.get("Ring") == null ||
                                                 tmp.get("Ring") != null &&
                                         tmp.get("Ring").equals(itemFile.get("Ring"))
                                 )*/
                         ) {
                             found = true;
                             itemJira = tmp;
                             break;
                         }
                     }
                     String deleted = (String) itemFile.get("Deleted");
                     String dup = (String) itemFile.get("Dup");
                     boolean delete = deleted != null && deleted.trim().length() > 0 || dup != null && dup.trim().length() > 0;
                     if (found) {
                         System.out.println("already in Jira:\n");
                         System.out.println("\tJira:" + itemJira);
                         System.out.println("\tFile:" + itemFile);
                         if (delete) {
                             System.out.println("delete:" + itemJira.get("Name"));
                             //iio.delete("/rest/api/2/issue/" + itemJira.get("id"));
                         } else {
                             if (itemJira.get("assignee") == null && itemFile.get("assignee") != null && itemFile.get("assignee").toString().length() > 0) {
                                 System.out.println("should assign:" + itemJira + " to " + itemFile.get("assignee"));
                                 //iio.put("/rest/api/2/issue/" + itemJira.get("id") + "/assignee", new JSONObject().put("name", itemFile.get("assignee")));
                             }
                         }
                     } else {
                         if (! delete) {
                             itemsDiff.add(itemFile);
                             System.out.println("adding to Jira:" + itemFile);
                         }
                     }
                 }
                 iio.postToJiraBulk(itemsDiff);
             }

             //new ItemIO().initFromJira(null);

             //iio.listUsers();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public static void main(String[] args) {

        try {
            Map<String, Integer> counts1 = new HashMap<>();
            Map<String, Set<String>> counts2 = new HashMap<>();
            ItemIO iio = new ItemIO();
            iio.username = args[0];
            iio.password = args[1];
            iio.initFromJira(false);
            //iio.initFromLastJiraRequest(false);
            for (Map<String, Object> item : iio.fromJira) {
                try {
                    String id = (String) item.get("id");

                    /*if (!id.equals("NTTR-2975")) {
                        continue;
                    }*/

                    System.out.println("read " + id);
                    JSONObject response = iio.get("/rest/api/latest/issue/" + id + "?expand=changelog");
                    System.out.println("response:" + response.toString());
                    JSONObject changelog = response.getJSONObject("changelog");
                    JSONArray histories = changelog.getJSONArray("histories");
                    aggregate(histories, id, counts1, counts2);

                    JSONObject fields = response.getJSONObject("fields");
                    JSONObject comment = fields.getJSONObject("comment");
                    JSONArray comments = comment.getJSONArray("comments");
                    aggregate(comments, id, counts1, counts2);

                    //System.out.println("response:" + changelog.toString());
                } catch (Exception e) {e.printStackTrace(System.out);}
            }
            for (Map.Entry<String, Integer> e : counts1.entrySet()) {
                 System.out.println(e.getKey() + "," + e.getValue() + "," + counts2.get(e.getKey()).size());
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    private static void aggregate(JSONArray authors, String id, Map<String, Integer> counts1, Map<String, Set<String>> counts2) {
        for (int i = 0; i < authors.length(); i++) {
            String key = ((JSONObject) authors.get(i)).getJSONObject("author").getString("key");
            //System.out.println(key);

            Integer count1 = counts1.get(key);
            if (count1 == null) {
                count1 = 0;
            }
            count1++;
            counts1.put(key, count1);

            Set<String> count2 = counts2.get(key);
            if (count2 == null) {
                count2 = new HashSet<>();
                counts2.put(key, count2);
            }
            count2.add(id);
        }

    }

    public void postToJiraBulk(List<Map<String, Object>> items) {
        Cluster.setAnonymize(false);
        for (Map<String, Object> item : items) {
            postToJira (item);
        }
    }

    private void postToJira (Map<String, Object> item) {
        System.out.println("working on " + item);
        String id = null;
        JSONObject root = new JSONObject();

        // create
        try {
            //Deleted  Dup     Name     Description   Topic   Ring     Category           Strategic Topic
            //unused   unused  summary  description   Topic   status   customfield_13501  "Strategic Topic"

            String name = (String) item.get("Name");
            String description = (String) item.get("Description");
            String topic = (String) ((Map<Cluster, String>) item.get("Values")).get(Cluster.TOPIC);
            String ring = (String) item.get("Ring");
            String category = (String) item.get("Category");
            String strategicTopic = (String) ((Map<Cluster, String>) item.get("Values")).get(Cluster.STRATEGIC_TOPIC);

            JSONObject fields = new JSONObject();
            root.put("fields", fields);
            fields.put("project", new JSONObject().put("key", "NTTR"));
            fields.put("summary", name);
            if (description != null) {
               fields.put("description", description);
            }
            if (topic != null && topic.trim().length() > 0) {
                fields.put("customfield_13503", new JSONObject().put("value", topic));
            }
            if (category != null && category.trim().length() > 0) {
               fields.put("customfield_13501", new JSONObject().put("value", category));
            }
            if (strategicTopic != null && strategicTopic.trim().length() > 0) {
                fields.put("customfield_13502", new JSONObject().put("value", strategicTopic));
            }
            fields.put("issuetype", new JSONObject().put("name", "Item"));

            System.out.println("creating:" + root.toString());
            JSONObject json2 = post ("/rest/api/2/issue/", root, 201);
            System.out.println("created:" + json2.toString());

            try {
                id = json2.get("id").toString();
            } catch (Exception e) {
                System.out.println("root=" + root.toString());
                System.out.println("json2=" + json2.toString());
                e.printStackTrace(System.out);
            }

        } catch (Exception e) {
            System.out.println("root=" + root.toString());
            e.printStackTrace(System.out);
        }

        // transition
        if (id != null) {
            String targetStatus = (String) item.get("Ring");
            if (targetStatus != null && targetStatus.trim().length() > 0) {
                int idx = statusOrder.indexOf(targetStatus.toLowerCase());
                if (idx < 0) {
                    System.out.println("targetStatus " + targetStatus + " not found.");
                    System.exit(0);
                }
                for (int sIdx = 0; sIdx <= idx; sIdx++) {
                    try {

                        JSONObject json2 = get("/rest/api/2/issue/" + id + "/transitions");

                        JSONArray json2Array = null;
                        try {
                            json2Array = (JSONArray) json2.get("transitions");
                        } catch (Exception e) {
                            System.out.println("json2:" + json2.toString());
                            e.printStackTrace(System.out);
                        }
                        if (json2Array != null) {
                            for (int i = 0; i < json2Array.length(); i++) {
                                JSONObject jo = (JSONObject) json2Array.get(i);
                                String statusName = ((String) jo.get("name")).toLowerCase();
                                if (statusOrder.indexOf(statusName) >= 0) {
                                    JSONObject jsonT = new JSONObject();
                                    jsonT.put("transition", new JSONObject().put("id", jo.get("id")));
                                    post("/rest/api/2/issue/" + id + "/transitions", jsonT, 204);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace(System.out);
                    }
                }
            }
        }
        //System.exit(0);
    }

    private void listUsers(String[] usernames) throws Exception {
        for (String username : usernames) {
            JSONObject jo = get("/rest/api/2/user/search?username=aUsername");
            System.out.println(jo.toString());
        }
    }

    private JSONObject get (String uri) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(jiraHost + uri);
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
        httpGet.addHeader(new BasicScheme().authenticate(creds, httpGet, null));

        CloseableHttpResponse response= httpclient.execute(httpGet);

        HttpEntity entityR = response.getEntity();
        JSONObject jsonR = null;
        String jsonRString = EntityUtils.toString(entityR);
        if (jsonRString.startsWith("[")) {
            jsonRString = "{\"array\" : " + jsonRString + "}";
        }
        try {
            jsonR = new JSONObject(jsonRString);
        } catch (Exception e) {
            System.out.println("String: " + jsonRString);
            e.printStackTrace(System.out);
        }

        EntityUtils.consume(entityR);
        response.close();

        return jsonR;
    }

    private JSONObject delete (String uri) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpDelete httpDelete = new HttpDelete(jiraHost + uri);
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
        httpDelete.addHeader(new BasicScheme().authenticate(creds, httpDelete, null));

        CloseableHttpResponse response= httpclient.execute(httpDelete);

        HttpEntity entityR = response.getEntity();
        JSONObject jsonR = null;
        if (entityR != null) {
            String jsonRString = EntityUtils.toString(entityR);
            if (jsonRString.trim().length() > 0) {
                if (jsonRString.startsWith("[")) {
                    jsonRString = "{\"array\" : " + jsonRString + "}";
                }
                try {
                    System.out.println("got:" + jsonRString);
                    jsonR = new JSONObject(jsonRString);
                } catch (Exception e) {
                    System.out.println("String: " + jsonRString);
                    e.printStackTrace(System.out);
                }
            } else {
                jsonR = null;
            }
        }

        EntityUtils.consume(entityR);
        response.close();

        return jsonR;
    }

    private JSONObject put (String uri, JSONObject payload) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpPut httpPut = new HttpPut(jiraHost + uri);
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);

        StringEntity entity = new StringEntity(payload.toString(), "UTF-8");
        httpPut.setEntity(entity);

        httpPut.addHeader(new BasicScheme().authenticate(creds, httpPut, null));
        httpPut.setHeader("Accept", "application/json");
        httpPut.setHeader("Content-type", "application/json");

        CloseableHttpResponse response= httpclient.execute(httpPut);

        System.out.println("uri=" + jiraHost + uri);
        System.out.println("status:" + response.getStatusLine().getStatusCode());

        HttpEntity entityR = response.getEntity();
        JSONObject jsonR = null;
        if (entityR != null) {
            String jsonRString = EntityUtils.toString(entityR);
            if (jsonRString.trim().length() > 0) {
                if (jsonRString.startsWith("[")) {
                    jsonRString = "{\"array\" : " + jsonRString + "}";
                }
                try {
                    System.out.println("got:" + jsonRString);
                    jsonR = new JSONObject(jsonRString);
                } catch (Exception e) {
                    System.out.println("String: " + jsonRString);
                    e.printStackTrace(System.out);
                }
            } else {
                jsonR = null;
            }
        }

        EntityUtils.consume(entityR);
        response.close();

        return jsonR;
    }

    private JSONObject post (String uri, JSONObject request, int expectedSC) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(jiraHost + uri);

        // auth
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
        httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost, null));

        StringEntity entity = new StringEntity(request.toString(), "UTF-8");

        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = httpclient.execute(httpPost);

        if (response.getStatusLine().getStatusCode() != expectedSC) {
            System.out.println("post failed:" + response.getStatusLine() + ", reuqest was:" + request.toString());
        }

        HttpEntity entityR = response.getEntity();

        JSONObject jsonR;

        if (entityR != null) {
            jsonR = new JSONObject(EntityUtils.toString(entityR));
            EntityUtils.consume(entityR);
        } else {
            jsonR = null;
        }
        response.close();


        return jsonR;
    }


}
