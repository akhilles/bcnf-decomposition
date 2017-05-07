import java.util.HashSet;
import java.util.Set;

/**
 * Created by akhil on 5/6/2017.
 */
public class FuncDep {
    Set<Character> lhs, rhs;

    public FuncDep(Set<Character> left, Set<Character> right){
        lhs = new HashSet<>(left);
        rhs = new HashSet<>(right);
    }

    public FuncDep(String left, String right){
        lhs = new HashSet<>();
        rhs = new HashSet<>();
        for (char toAddLeft: left.toCharArray()){
            lhs.add(toAddLeft);
        }
        for (char toAddRight: right.toCharArray()){
            rhs.add(toAddRight);
        }
    }

    public Set<Character> setUnion(){
        Set<Character> union = new HashSet<>(lhs);
        union.addAll(rhs);
        return union;
    }

    public FuncDep combine(FuncDep other){
        FuncDep sum = new FuncDep(other.lhs, rhs);

        sum.rhs.removeAll(other.lhs);
        sum.lhs.removeAll(rhs);
        sum.lhs.addAll(lhs);
        sum.rhs.addAll(other.rhs);
        sum.rhs.removeAll(sum.lhs);

        return sum;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null || !FuncDep.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        FuncDep other = (FuncDep) obj;
        return other.lhs.equals(lhs) && other.rhs.equals(rhs);
    }


    public String toString(){
        return lhs + " -> " + rhs;
    }
}
