package net.acidfrog.kronos.toolkit.benchmark;

public class StringFactory implements ArgumentFactory<String> {

    private static final String[] WORDS = {
        "lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit", "sed", "do",
        "eiusmod", "tempor", "incididunt", "ut", "labore", "et", "dolore", "magna", "aliqua", "Ut",
        "enim", "ad", "minim", "veniam", "quis", "nostrud", "exercitation", "ullamco", "laboris",
        "nisi", "ut", "aliquip", "ex", "ea", "commodo", "consequat", "duis", "aute", "irure", "dolor",
        "in", "reprehenderit", "in", "voluptate", "velit", "esse", "cillum", "dolore", "eu", "fugiat",
        "nulla", "pariatur", "excepteur", "sint", "occaecat", "cupidatat", "non", "proident", "sunt",
        "culpa", "qui", "officia", "deserunt", "mollit", "anim", "id", "est", "laborum"
    };

    @Override
    public String create() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(WORDS[(int) (Math.random() * WORDS.length)]);
            sb.append(" ");
        }
        
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        sb.append("\033[1D.");

        return sb.toString();
    }
   
}
