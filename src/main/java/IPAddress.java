public class IPAddress {

    int[] numbers = new int[4];

    public IPAddress(int[] numbers) {
        this.numbers = numbers.clone();
    }

    public IPAddress(String ip) {
        String[] stringNumbers = ip.split("\\.");

        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = Integer.parseInt(stringNumbers[i]);
        }
    }

    @Override
    public String toString() {
        String[] stringNumbers = new String[numbers.length];

        for (int i = 0; i < numbers.length; i++) {
            stringNumbers[i] = Integer.toString(numbers[i]);
        }

        return String.join(".", stringNumbers);
    }

    public boolean equals(IPAddress ipAddress) {
        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i] != ipAddress.numbers[i]) {
                return false;
            }
        }

        return true;
    }

    public boolean lessThan(IPAddress ipAddress) {
        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i] > ipAddress.numbers[i]) {
                return false;
            }
        }

        return (!equals(ipAddress));
    }

    public boolean greaterThan(IPAddress ipAddress) {
        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i] < ipAddress.numbers[i]) {
                return false;
            }
        }

        return (!equals(ipAddress));
    }
}
