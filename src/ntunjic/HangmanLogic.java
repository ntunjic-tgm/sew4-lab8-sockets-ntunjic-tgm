package ntunjic;

import java.util.ArrayList;
import java.util.List;

public class HangmanLogic {
    private String word;
    private StringBuilder hiddenWord;
    private int tries;


    public HangmanLogic(String word) {
        this.word = word.toUpperCase();
        this.tries = 7;
        this.hiddenWord = new StringBuilder();
        this.hiddenWord.append("_".repeat(word.length()));
    }

    public boolean check(char a) { // hier ist das überprüfen der Char.
        List<Integer> occ = new ArrayList<>();
        boolean rval = false;

        for (int i = this.word.indexOf(a); i >= 0; i = this.word.indexOf(a, i + 1)) {
            occ.add(i);
        }
        if (occ.size() == 0) {
            this.tries--;
        } else {
            rval = true;
            for (Integer index : occ) {
                this.hiddenWord.setCharAt(index, this.word.charAt(index)); // falls vorhanden -> Buchstabe wird angezeigt
            }
            if (this.word.equals(this.hiddenWord.toString())) this.tries = -1; // nicht vorhanden -> -1 Versuche
        }

        return rval;
    }

    public int getTries() {
        return this.tries;
    }

    public String getWord() {
        return this.word;
    }

    public String getHiddenWord() {
        return this.hiddenWord.toString();
    }
}
