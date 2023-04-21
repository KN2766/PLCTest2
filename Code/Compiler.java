package PLCAssignment.Code;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Compiler {
    public static void main(String[] args) throws FileNotFoundException{
        List<Token> output = new LinkedList<>();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name of the input file: ");

        String fileName = scanner.nextLine();
        File inputFile = new File(fileName);
        
        Scanner fileScanner = new Scanner(inputFile);
        String string = "";
        while(fileScanner.hasNext()){
            string += fileScanner.nextLine() + "\n";
        }
        fileScanner.close();
        Lexer lex = new Lexer(string);
        
        output = lex.printTokens();
        System.out.println(output);

        Parser par = new Parser(output);
        par.parse();
    }
}
