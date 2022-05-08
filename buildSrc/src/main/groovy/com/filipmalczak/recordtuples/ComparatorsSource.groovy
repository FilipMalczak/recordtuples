package com.filipmalczak.recordtuples

import com.github.filipmalczak.recordtuples.TupleDefinition
import groovy.transform.Canonical

@Canonical
class ComparatorsSource {
    String basePkg
    List<TupleDefinition> definitions

    private String comparingX(String name, int x, int maxX){
"""        static <T> Comparator<${name}<${(0..maxX).collect({ it == x ? "T" : "?" }).join(", ")}>> comparing$x(Comparator<T> delegate){
            return (u1, u2) -> delegate.compare(u1.get$x(), u2.get$x());
        }"""
    }

    private String comparingComparableX(String name, int x, int maxX){
"""        static <T extends Comparable<T>> Comparator<${name}<${(0..maxX).collect({ it == x ? "T" : "?" }).join(", ")}>> comparing$x(){
            return comparing$x(Comparable::compareTo);
        }"""
    }

    private String naturalOrdering(String name, int maxX){
        def genericsDef = (0..maxX).collect { "T$it extends Comparable<T$it>" }.join(", ")
        def genericsNames = (0..maxX).collect { "T$it" }.join(", ")
        List<String> lines = [
            "            return (u1, u2) -> {"
        ]
        if (maxX > 0){
            lines += (0..(maxX-1)).collect {
"""                int by$it = u1.v${it}().compareTo(u2.v${it}());
                if (by$it != 0) return by$it;
"""
            }
        }
        lines += [ "                return u1.v${maxX}().compareTo(u2.v${maxX}());",
                   "            };" ]
        """        static <$genericsDef> Comparator<${name}<$genericsNames>> naturalOrdering(){
${lines.join("\n")}
        }"""
    }

    private String groupFor(TupleDefinition definition) {
"""    public static final class ${definition.name()}s {
        private ${definition.name()}s(){}

${(0..(definition.size()-1))
        .collect({
"""${comparingX(definition.name(), it, definition.size()-1)}

${comparingComparableX(definition.name(), it, definition.size()-1)}"""
        }).join("\n\n")}

${naturalOrdering(definition.name(), definition.size()-1)}
    }"""
    }

    String toString(){
"""package com.github.filipmalczak.recordtuples;

import java.util.Comparator;

public final class TupleComparators {
    private TupleComparators(){}

${definitions.collect(this.&groupFor).join("\n\n")}
}
"""
    }
}
