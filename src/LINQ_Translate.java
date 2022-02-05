import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale;

public class LINQ_Translate extends SqlParserBaseListener{


    @Override
    public void enterRoot(SqlParser.RootContext ctx) {
        super.enterRoot(ctx);
        System.out.print("var result = ");
    }

    Hashtable<String, ArrayList<String>> keys = new Hashtable<String, ArrayList<String>>();
    String key = null;

    @Override
    public void enterSelectElements(SqlParser.SelectElementsContext ctx) {
        super.enterSelectElements(ctx);
        key = "select";
        keys.put(key, new ArrayList<>());
        if (ctx.STAR() != null){
            keys.get(key).add("*");
        }
    }

    @Override
    public void enterFromClause(SqlParser.FromClauseContext ctx) {
        super.enterFromClause(ctx);
        if(ctx.FROM() != null){
            key = "from";
            keys.put(key, new ArrayList<>());
        }
    }

    @Override
    public void enterOrderByClause(SqlParser.OrderByClauseContext ctx) {
        super.enterOrderByClause(ctx);
        key = "orderby";
        keys.put(key, new ArrayList<>());
    }

    @Override
    public void enterSimpleId(SqlParser.SimpleIdContext ctx) {
        super.enterSimpleId(ctx);
        keys.get(key).add(ctx.getText());
    }

    @Override
    public void exitRoot(SqlParser.RootContext ctx) {
        super.exitRoot(ctx);
        while(keys.size() != 0){
            String table_name = keys.get("from").get(0).toLowerCase(Locale.ROOT);
            System.out.println("from " + table_name.charAt(0) + " in " + table_name);
            keys.remove("from");
            if(keys.get("orderby") != null) {
                String id = keys.get("orderby").get(0).toLowerCase(Locale.ROOT);
                System.out.println("orderby " + table_name.charAt(0) + "." + id);
                keys.remove("orderby");
            }
            if(keys.get("select") != null) {
                int size = keys.get("select").size();
                if(size > 1){
                    System.out.print("select new {");
                    String first_id = keys.get("select").get(0).toLowerCase(Locale.ROOT);
                    System.out.print(table_name.charAt(0) + "." + first_id);
                    for (int i = 1; i<size; i++) {
                        String id = keys.get("select").get(0).toLowerCase(Locale.ROOT);
                        System.out.print(", " + table_name.charAt(0)+ "." + id);
                    }
                    System.out.print("}");
                    keys.remove("select");
                }else{
                    String id = keys.get("select").get(0).toLowerCase(Locale.ROOT);
                    if(id.equals("*")){
                        System.out.print("select " + table_name.charAt(0));
                    }else{
                        System.out.print("select " + table_name.charAt(0) + "." + id);
                    }
                    keys.remove("select");
                }
            }
        }
        System.out.println(";");
    }
}
