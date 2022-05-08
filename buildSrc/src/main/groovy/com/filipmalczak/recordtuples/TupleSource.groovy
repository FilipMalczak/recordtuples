package com.filipmalczak.recordtuples

import com.github.filipmalczak.recordtuples.TupleDefinition

class TupleSource {
    String basePkg
    String name
    String tailName
    int size

    private String typedTailName
    private String allGenerics
    private String reverseGenerics
    private String tailGenerics
    private String recordMembers
    private String fieldNames
    private String fieldNamesTail
    private String reverseFieldNames

    TupleSource(String basePkg, TupleDefinition definition) {
        this(basePkg, definition.name, definition.tailName, definition.size)
    }

    TupleSource(String basePkg, String name, String tailName, int size) {
        this.basePkg = basePkg
        this.name = name
        this.tailName = tailName
        this.size = size
        init()
    }

    private void init(){
        def x = size-1
        allGenerics = (0..x).collect { "T$it" }.join(", ")
        reverseGenerics = (x..0).collect { "T$it" }.join(", ")
        tailGenerics = x > 0 ? (1..x).collect { "T$it" }.join(", ") : ""
        recordMembers = (0..x).collect { "T$it v$it" }.join(", ")
        fieldNames = (0..x).collect { "v$it" }.join(", ")
        fieldNamesTail = x > 0 ? (1..x).collect { "v$it" }.join(", ") : ""
        reverseFieldNames = (x..0).collect { "v$it" }.join(", ")
        if (size > 1){
            typedTailName = "$tailName<$tailGenerics>"
        } else {
            typedTailName = tailName
        }
    }

    private String getX(int x){
        "    public T$x get$x() { return v$x; }"
    }

    private String withX(int x){
        "    public <T> ${name}<${allGenerics.replace("T$x", "T")}> with$x(T v) { return of(${fieldNames.replace("v$x", "v")}); }"
    }

    private String mapX(int x){
        "    public <T> ${name}<${allGenerics.replace("T$x", "T")}> map$x(Function<T$x, T> mapper) { return with$x(mapper.apply(v$x)); }"
    }

    private String forEachX(body){
        (0..(size-1)).collect(body).join("\n")
    }

    private String recordDefinition(){
        //todo add static ThisType of(T0, Tail)
            """public record ${name}<${allGenerics}>(
    ${recordMembers.replaceAll(", ", ",\n    ")}
) implements Tuple<T0, $typedTailName> {
    public static <${allGenerics}> ${name}<${allGenerics}> of($recordMembers){
        return new ${name}<>($fieldNames);
    }
    
    @Override
    public T0 getHead(){ return v0; }
    
    @Override
    public $typedTailName getTail(){ return ${tailName}.of(${fieldNamesTail}); }

    @Override
    public int size(){ return ${size}; }
    
    public $name<$reverseGenerics> reverse(){ return of($reverseFieldNames); }

${forEachX(this.&getX)}

${forEachX(this.&withX)}

${forEachX(this.&mapX)}
}
"""
    }

    String toString() {
"""package ${basePkg};

import java.util.function.Function;

${recordDefinition()}
"""
    }
}
