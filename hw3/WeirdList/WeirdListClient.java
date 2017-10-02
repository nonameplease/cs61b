/** Functions to increment and sum the elements of a WeirdList.
 *  @author Scott Shao
 * */
class WeirdListClient {

    /** Return the result of adding N to each element of L. */
    static WeirdList add(WeirdList L, int n) {
        Add add = new Add(n);
        return L.map(add);
    }

    /** Return the sum of the elements in L. */
    static int sum(WeirdList L) {
        Sum sum = new Sum();
        L.map(sum);
        return sum.getTotal();

    }

    /**
     * A nested class that is used to add a int to each element
     * of L.
     */
    private static class Add implements IntUnaryFunction {
        /**
         * A class attribute of Add class.
         */
        private int total;

        /**
         * Let the total be the first value.
         * @param first Number that should be added to
         *              each element.
         */
        public Add(int first) {
            total = first;
        }

        @Override
        public int apply(int x) {
            return total + x;
        }
    }

    /**
     * A nested class that is used to find the sum.
     */
    private static class Sum implements IntUnaryFunction {
        /**
         * A class attribute of Sum class.
         */
        private int total;

        /**
         * Initialize total to equal to zero.
         */
        public Sum() {
            total = 0;
        }

        @Override
        public int apply(int x) {
            total += x;
            return total;
        }

        /**
         * A method that return the private int total.
         * @return Return the sum of all elements.
         */
        public int getTotal() {
            return total;
        }

    }

    /* As with WeirdList, you'll need to add an additional class or
     * perhaps more for WeirdListClient to work. Again, you may put
     * those classes either inside WeirdListClient as private static
     * classes, or in their own separate files.

     * You are still forbidden to use any of the following:
     *       if, switch, while, for, do, try, or the ?: operator.
     */
}
