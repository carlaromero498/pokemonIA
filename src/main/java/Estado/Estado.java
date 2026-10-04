package Estado;

/*Opção 2: esta linha: */
public record Estado(int line, int column){}

/* Opção 1: fazer tudo à mão
public class Estado {
    
    private final int line; //upon instaciation the values never change
    private final int column;

    public Estado(int line, int column){
        this.line = line;
        this.column = column;
    }

    public int getLine(){
        return line;
    }

    public int getColumn(){
        return column;
    }

    @Override 
    public boolean equals(Object o){
        if(this == o) return true; //same object
        if(!(o instanceof Estado)) return false; //returns false if null or from other class
        Estado outro = (Estado) o;
        return line == outro.line && column == outro.column; //compares if position is the same
    }

    @Override 
    public int hashCode(){
        return Objects.hash(line, column);
    }
        
}*/
