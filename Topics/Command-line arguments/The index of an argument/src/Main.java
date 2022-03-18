class Problem {
    public static void main(String[] args) {
        int result = -1;
        for (int i = 0; i < args.length; i++) {
            if ("test".equals(args[i])) {
                result = i;
            }
        }
        System.out.println(result);
    }
}