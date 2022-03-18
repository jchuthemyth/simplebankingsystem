class Problem {
    public static void main(String[] args) {
        System.out.println(display(args));
    }

    static String display(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if ("mode".equals(args[i]) && i % 2 == 0) {
                return args[i + 1];
            }
        }
        return "default";
    }
}
