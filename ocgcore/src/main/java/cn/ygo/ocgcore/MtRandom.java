package cn.ygo.ocgcore;


class MtRandom {
    private static final int N = 624;
    private static final int M = 397;
    private static final MtRandom RANDOM = new MtRandom();
    private int _current;
    private long _left = 1;
    private long[] _state = new long[N];

    public static long random() {
        return RANDOM.Rand();
    }

    private MtRandom() {
        Init();
    }

    private MtRandom(long seed) {
        Init(seed);
    }

    void Init() {
        Init(19650218);
    }

    void Init(long seed) {
        _state[0] = seed & 4294967295L;
        for (int j = 1; j < N; ++j) {
            _state[j] = (long) (1812433253L * (_state[j - 1] ^ (_state[j - 1] >> 30)) + j);
            _state[j] &= 4294967295L;
        }
    }

    long Rand() {
        long y;
        if (0 == --_left)
            NextState();
        y = _state[_current++];
        y ^= (y >> 11);
        y ^= (y << 7) & 0x9d2c5680L;
        y ^= (y << 15) & 0xefc60000L;
        y ^= (y >> 18);
        return y;
    }

    void Reset(long rs) {
        Init(rs);
        NextState();
    }

    private void NextState() {
        int k = 0;
        for (int i = N - M + 1; --i != 0; ) {
            _state[k] = (_state[k + M] ^ Twist(_state[k], _state[k + 1]));
            k = k + 1;
        }
        for (int i = M; --i != 0; ) {
            _state[k] = (_state[k + M - N] ^ Twist(_state[k], _state[k + 1]));
            k = k + 1;
        }
        _state[k] = (_state[k + M - N] ^ Twist(_state[k], _state[0]));
        _left = N;
        _current = 0;
    }

    private static long Twist(long u, long v) {
        return ((MixBits(u, v) >> 1) ^ ((v & 1L) != 0 ? 2567483615L : 0L));
    }

    private static long MixBits(long u, long v) {
        return (u & 2147483648L) | (v & 2147483647L);
    }

}
