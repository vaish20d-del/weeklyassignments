import java.util.*;

class Transaction {

    int id;
    int amount;
    String merchant;
    String account;
    long time;

    public Transaction(int id, int amount, String merchant, String account, long time) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.time = time;
    }
}

class FraudDetector {

    public List<int[]> findTwoSum(List<Transaction> txns, int target) {

        Map<Integer, Transaction> map = new HashMap<>();
        List<int[]> result = new ArrayList<>();

        for (Transaction t : txns) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                result.add(new int[]{map.get(complement).id, t.id});
            }

            map.put(t.amount, t);
        }

        return result;
    }

    public List<List<Integer>> findKSum(int[] nums, int k, int target) {

        List<List<Integer>> res = new ArrayList<>();
        kSumHelper(nums, k, target, 0, new ArrayList<>(), res);
        return res;
    }

    private void kSumHelper(int[] nums, int k, int target, int start,
                            List<Integer> path, List<List<Integer>> res) {

        if (k == 0 && target == 0) {
            res.add(new ArrayList<>(path));
            return;
        }

        if (k == 0) return;

        for (int i = start; i < nums.length; i++) {

            path.add(nums[i]);
            kSumHelper(nums, k - 1, target - nums[i], i + 1, path, res);
            path.remove(path.size() - 1);
        }
    }

    public Map<String, List<Transaction>> detectDuplicates(List<Transaction> txns) {

        Map<String, List<Transaction>> map = new HashMap<>();

        for (Transaction t : txns) {

            String key = t.amount + "_" + t.merchant;

            map.computeIfAbsent(key, k -> new ArrayList<>()).add(t);
        }

        return map;
    }
}