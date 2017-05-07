import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by akhil on 5/6/2017.
 */
public class BCNFSolver {
    private String attributes;
    private ArrayList<FuncDep> confirmedDeps;

    public BCNFSolver(String attributes, ArrayList<String[]> deps){
        this.attributes = attributes;
        System.out.println("GIVEN ATTRIBUTES: [" + attributes + "]\n");
        confirmedDeps = new ArrayList<>();
        System.out.println("GIVEN FUNCTIONAL DEPENDENCIES:");
        for (String[] dep: deps){
            FuncDep newDep = new FuncDep(dep[0], dep[1]);
            confirmedDeps.add(newDep);
            System.out.println(newDep);
        }
        generateDeps();

        System.out.println("\nNON-TRIVIAL FUNCTIONAL DEPENDENCIES:");
        for (FuncDep fd: confirmedDeps){
            System.out.println(fd);
        }
    }

    public ArrayList<String> getDecomp(){
        ArrayList<String> decomp = new ArrayList<>();
        Stack<String> S = new Stack<>();
        String R = attributes;

        S.push(R);
        while(!S.isEmpty()){
            String A = S.pop();
            ArrayList<FuncDep> violations = getBCNFViolations(A);
            if (violations.isEmpty()){
                if (!decomp.contains(A)) decomp.add(A);
            } else {
                System.out.print("BCNF violation " + violations.get(0) + " of " + A + ": ");
                String union = setToString(violations.get(0).setUnion());
                if (!S.contains(union) && !decomp.contains(union)) S.push(union);

                String missing = missingRHS(violations.get(0).rhs, A);
                if (!S.contains(missing) && !decomp.contains(missing)) S.push(missing);
                System.out.println("[" + union + ", " + missing + "]");
            }
        }

        return decomp;
    }

    private String missingRHS(Set<Character> rhs, String R){
        for (char c: rhs){
            R = R.replace("" + c, "");
        }
        return R;
    }

    private String setToString(Set<Character> set){
        String R = "";
        for (char c: set){
            R += c;
        }
        return R;
    }

    public void generateDeps(){
        // generate all functional dependencies
        while(true){
            boolean depFound = false;
            ArrayList<FuncDep> toAdd = new ArrayList<>();

            for (FuncDep dep1: confirmedDeps){
                for (FuncDep dep2: confirmedDeps){
                    if (dep1.equals(dep2)) continue;
                    FuncDep sum = dep1.combine(dep2);
                    if (!confirmedDeps.contains(sum) && !toAdd.contains(sum)){
                        toAdd.add(sum);
                        depFound = true;
                    }
                }
            }

            confirmedDeps.addAll(toAdd);
            if (!depFound) break;
        }

        // remove trivial dependencies
        ArrayList<FuncDep> toRemove = new ArrayList<>();
        for (FuncDep dep1: confirmedDeps) {
            if (dep1.rhs.isEmpty() || dep1.lhs.isEmpty()) toRemove.add(dep1);
            for (FuncDep dep2 : confirmedDeps) {
                if (dep1.equals(dep2)) continue;
                if (dep1.lhs.equals(dep2.lhs) && dep1.rhs.size() > dep2.rhs.size() && !toRemove.contains(dep2)) toRemove.add(dep2);
                /*
                else if (dep2.lhs.containsAll(dep1.lhs) && dep1.rhs.containsAll(dep2.rhs) && !toRemove.contains(dep2)){
                    toRemove.add(dep2);
                }
                */
            }
        }
        confirmedDeps.removeAll(toRemove);
    }

    public ArrayList<FuncDep> getBCNFViolations(String relation){
        ArrayList<FuncDep> BCNFViolations = new ArrayList<>();
        for (FuncDep cd: confirmedDeps){
            if (isViolation(cd, relation)) BCNFViolations.add(cd);
        }
        return BCNFViolations;
    }

    public boolean isViolation(FuncDep cd, String relation){
        boolean valid = false;
        for (char r: cd.rhs){
            if (relation.contains("" + r)) valid = true;
        }
        if (!valid) return false;

        // lhs is subset of attributes
        for (char l: cd.lhs){
            if (!relation.contains("" + l)) return false;
        }
        // all attributes must appear in FuncDep
        for (char a: relation.toCharArray()){
            if (!cd.lhs.contains(a) && !cd.rhs.contains(a)) return true;
        }
        return false;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner scan = new Scanner(new File("sample_2.txt"));
        String attributes = scan.nextLine().replace(" ", "");

        ArrayList<String[]> givenDeps = new ArrayList<>();
        while(scan.hasNext()){
            String[] givenDep = scan.nextLine().replace(" ", "").split("->");
            givenDeps.add(givenDep);
        }

        BCNFSolver bcnfs = new BCNFSolver(attributes, givenDeps);
        System.out.println("\nDECOMPOSITION: ");
        ArrayList<String> BCNFViolations = bcnfs.getDecomp();
        System.out.println(BCNFViolations);
    }
}
