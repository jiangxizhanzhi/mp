package cn.cyberpeace.mp.base.util;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.code.ErrorCode;

import java.time.LocalDateTime;
import java.util.UUID;

public class IdUtils {

    static {
        // TODO: 2023/10/9 多实例需要优化这里的workid，每个实例一个不同的workId
        IdGeneratorOptions options = new IdGeneratorOptions((short) 1);
        YitIdHelper.setIdGenerator(options);
    }

    private IdUtils() {}


    public static String globalId(){
        return String.valueOf(YitIdHelper.nextId());
    }
}


class SnowWorkerM1 {

    /**
     * 基础时间
     */
    protected final long BaseTime;

    /**
     * 机器码
     */
    protected final short WorkerId;

    /**
     * 机器码位长
     */
    protected final byte WorkerIdBitLength;

    /**
     * 自增序列数位长
     */
    protected final byte SeqBitLength;

    /**
     * 最大序列数（含）
     */
    protected final int MaxSeqNumber;

    /**
     * 最小序列数（含）
     */
    protected final short MinSeqNumber;

    /**
     * 最大漂移次数（含）
     */
    protected final int TopOverCostCount;

    protected final byte _TimestampShift;
    protected final static byte[] _SyncLock = new byte[0];

    protected short _CurrentSeqNumber;
    protected long _LastTimeTick = 0;
    protected long _TurnBackTimeTick = 0;
    protected byte _TurnBackIndex = 0;

    protected boolean _IsOverCost = false;
    protected int _OverCostCountInOneTerm = 0;
    protected int _GenCountInOneTerm = 0;
    protected int _TermIndex = 0;

    public SnowWorkerM1(IdGeneratorOptions options) {
        BaseTime = options.BaseTime != 0 ? options.BaseTime : 1582136402000L;
        WorkerIdBitLength = options.WorkerIdBitLength == 0 ? 6 : options.WorkerIdBitLength;
        WorkerId = options.WorkerId;
        SeqBitLength = options.SeqBitLength == 0 ? 6 : options.SeqBitLength;
        MaxSeqNumber = options.MaxSeqNumber <= 0 ? (1 << SeqBitLength) - 1 : options.MaxSeqNumber;
        MinSeqNumber = options.MinSeqNumber;
        // TopOverCostCount = options.TopOverCostCount == 0 ? 2000 : options.TopOverCostCount;
        TopOverCostCount = options.TopOverCostCount;
        _TimestampShift = (byte) (WorkerIdBitLength + SeqBitLength);
        _CurrentSeqNumber = MinSeqNumber;
    }

    private void DoGenIdAction(OverCostActionArg arg) {

    }

    private void BeginOverCostAction(long useTimeTick) {

    }

    private void EndOverCostAction(long useTimeTick) {
        // if (_TermIndex > 10000) {
        //     _TermIndex = 0;
        // }
    }

    private void BeginTurnBackAction(long useTimeTick) {

    }

    private void EndTurnBackAction(long useTimeTick) {

    }

    private long NextOverCostId() {
        long currentTimeTick = GetCurrentTimeTick();

        if (currentTimeTick > _LastTimeTick) {
            EndOverCostAction(currentTimeTick);

            _LastTimeTick = currentTimeTick;
            _CurrentSeqNumber = MinSeqNumber;
            _IsOverCost = false;
            _OverCostCountInOneTerm = 0;
            _GenCountInOneTerm = 0;

            return CalcId(_LastTimeTick);
        }

        if (_OverCostCountInOneTerm >= TopOverCostCount) {
            EndOverCostAction(currentTimeTick);

            _LastTimeTick = GetNextTimeTick();
            _CurrentSeqNumber = MinSeqNumber;
            _IsOverCost = false;
            _OverCostCountInOneTerm = 0;
            _GenCountInOneTerm = 0;

            return CalcId(_LastTimeTick);
        }

        if (_CurrentSeqNumber > MaxSeqNumber) {
            _LastTimeTick++;
            _CurrentSeqNumber = MinSeqNumber;
            _IsOverCost = true;
            _OverCostCountInOneTerm++;
            _GenCountInOneTerm++;

            return CalcId(_LastTimeTick);
        }

        _GenCountInOneTerm++;
        return CalcId(_LastTimeTick);
    }

    private long NextNormalId() {
        long currentTimeTick = GetCurrentTimeTick();

        if (currentTimeTick < _LastTimeTick) {
            if (_TurnBackTimeTick < 1) {
                _TurnBackTimeTick = _LastTimeTick - 1;
                _TurnBackIndex++;
                // 每毫秒序列数的前5位是预留位，0用于手工新值，1-4是时间回拨次序
                // 支持4次回拨次序（避免回拨重叠导致ID重复），可无限次回拨（次序循环使用）。
                if (_TurnBackIndex > 4) {
                    _TurnBackIndex = 1;
                }
            }

            return CalcTurnBackId(_TurnBackTimeTick);
        }

        // 时间追平时，_TurnBackTimeTick清零
        if (_TurnBackTimeTick > 0) {
            EndTurnBackAction(_TurnBackTimeTick);
            _TurnBackTimeTick = 0;
        }

        if (currentTimeTick > _LastTimeTick) {
            _LastTimeTick = currentTimeTick;
            _CurrentSeqNumber = MinSeqNumber;

            return CalcId(_LastTimeTick);
        }

        if (_CurrentSeqNumber > MaxSeqNumber) {
            BeginOverCostAction(currentTimeTick);

            _TermIndex++;
            _LastTimeTick++;
            _CurrentSeqNumber = MinSeqNumber;
            _IsOverCost = true;
            _OverCostCountInOneTerm = 1;
            _GenCountInOneTerm = 1;

            return CalcId(_LastTimeTick);
        }

        return CalcId(_LastTimeTick);
    }

    private long CalcId(long useTimeTick) {
        long result = ((useTimeTick << _TimestampShift) +
                ((long) WorkerId << SeqBitLength) +
                (int) _CurrentSeqNumber);

        _CurrentSeqNumber++;
        return result;
    }

    private long CalcTurnBackId(long useTimeTick) {
        long result = ((useTimeTick << _TimestampShift) +
                ((long) WorkerId << SeqBitLength) + _TurnBackIndex);

        _TurnBackTimeTick--;
        return result;
    }

    protected long GetCurrentTimeTick() {
        long millis = System.currentTimeMillis();
        return millis - BaseTime;
    }

    protected long GetNextTimeTick() {
        long tempTimeTicker = GetCurrentTimeTick();

        while (tempTimeTicker <= _LastTimeTick) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tempTimeTicker = GetCurrentTimeTick();
        }

        return tempTimeTicker;
    }

    public long nextId() {
        synchronized (_SyncLock) {
            return _IsOverCost ? NextOverCostId() : NextNormalId();
        }
    }
}

/**
 * 雪花算法使用的参数
 * 参数说明，参考 README.md 的 “配置参数” 章节。
 */
class IdGeneratorOptions {

    /**
     * 雪花计算方法
     * （1-漂移算法|2-传统算法），默认1
     */
    public short Method = 1;

    /**
     * 基础时间（ms单位）
     * 不能超过当前系统时间
     */
    public long BaseTime = 1582136402000L;

    /**
     * 机器码
     * 必须由外部设定，最大值 2^WorkerIdBitLength-1
     */
    public short WorkerId = 0;

    /**
     * 机器码位长
     * 默认值6，取值范围 [1, 15]（要求：序列数位长+机器码位长不超过22）
     */
    public byte WorkerIdBitLength = 6;

    /**
     * 序列数位长
     * 默认值6，取值范围 [3, 21]（要求：序列数位长+机器码位长不超过22）
     */
    public byte SeqBitLength = 6;

    /**
     * 最大序列数（含）
     * 设置范围 [MinSeqNumber, 2^SeqBitLength-1]，默认值0，表示最大序列数取最大值（2^SeqBitLength-1]）
     */
    public short MaxSeqNumber = 0;

    /**
     * 最小序列数（含）
     * 默认值5，取值范围 [5, MaxSeqNumber]，每毫秒的前5个序列数对应编号是0-4是保留位，其中1-4是时间回拨相应预留位，0是手工新值预留位
     */
    public short MinSeqNumber = 5;

    /**
     * 最大漂移次数（含）
     * 默认2000，推荐范围500-10000（与计算能力有关）
     */
    public short TopOverCostCount = 2000;

    public IdGeneratorOptions() {

    }

    public IdGeneratorOptions(short workerId) {
        WorkerId = workerId;
    }
}

/**
 * Id生成时回调参数
 */
class OverCostActionArg {

    /**
     * 事件类型
     * 1-开始，2-结束，8-漂移
     */
    public int ActionType = 0;

    /**
     * 时间戳
     */
    public long TimeTick = 0;

    /**
     * 机器码
     */
    public short WorkerId = 0;

    /**
     *
     */
    public int OverCostCountInOneTerm = 0;

    /**
     * 漂移期间生产ID个数
     */
    public int GenCountInOneTerm = 0;

    /**
     * 漂移周期
     */
    public int TermIndex = 0;

    public OverCostActionArg(short workerId, long timeTick, int actionType, int overCostCountInOneTerm, int genCountWhenOverCost, int index) {
        ActionType = actionType;
        TimeTick = timeTick;
        WorkerId = workerId;
        OverCostCountInOneTerm = overCostCountInOneTerm;
        GenCountInOneTerm = genCountWhenOverCost;
        TermIndex = index;
    }
}

class SnowWorkerM2 extends SnowWorkerM1 {

    public SnowWorkerM2(IdGeneratorOptions options) {
        super(options);
    }

    @Override
    public long nextId() {
        synchronized (_SyncLock) {
            long currentTimeTick = GetCurrentTimeTick();

            if (_LastTimeTick == currentTimeTick) {
                if (_CurrentSeqNumber++ > MaxSeqNumber) {
                    _CurrentSeqNumber = MinSeqNumber;
                    currentTimeTick = GetNextTimeTick();
                }
            } else {
                _CurrentSeqNumber = MinSeqNumber;
            }

            if (currentTimeTick < _LastTimeTick) {
                throw new MpRuntimeException(BaseErrorCode.ERROR);
            }

            _LastTimeTick = currentTimeTick;
            long result = ((currentTimeTick << _TimestampShift) + ((long) WorkerId << SeqBitLength) + (int) _CurrentSeqNumber);

            return result;
        }

    }
}

class YitIdHelper {

    private static DefaultIdGenerator idGenInstance = null;

    public static DefaultIdGenerator getIdGenInstance() {
        return idGenInstance;
    }

    /**
     * 设置参数，建议程序初始化时执行一次
     */
    public static void setIdGenerator(IdGeneratorOptions options) {
        idGenInstance = new DefaultIdGenerator(options);
    }

    /**
     * 生成新的Id
     * 调用本方法前，请确保调用了 SetIdGenerator 方法做初始化。
     *
     * @return
     */
    public static long nextId() {
        if (idGenInstance == null)
            throw new MpRuntimeException("Please initialize Yitter.IdGeneratorOptions first.");

        return idGenInstance.newLong();
    }
}

class DefaultIdGenerator {

    private static SnowWorkerM1 _SnowWorker = null;

    public DefaultIdGenerator(IdGeneratorOptions options) {
        if (options == null) {
            throw new MpRuntimeException("options error.");
        }

        // 1.BaseTime
        if (options.BaseTime < 315504000000L || options.BaseTime > System.currentTimeMillis()) {
            throw new MpRuntimeException("BaseTime error.");
        }

        // 2.WorkerIdBitLength
        if (options.WorkerIdBitLength <= 0) {
            throw new MpRuntimeException("WorkerIdBitLength error.(range:[1, 21])");
        }
        if (options.WorkerIdBitLength + options.SeqBitLength > 22) {
            throw new MpRuntimeException("error：WorkerIdBitLength + SeqBitLength <= 22");
        }

        // 3.WorkerId
        int maxWorkerIdNumber = (1 << options.WorkerIdBitLength) - 1;
        if (maxWorkerIdNumber == 0) {
            maxWorkerIdNumber = 63;
        }
        if (options.WorkerId < 0 || options.WorkerId > maxWorkerIdNumber) {
            throw new MpRuntimeException(
                    "WorkerId error. (range:[0, " + (maxWorkerIdNumber > 0 ? maxWorkerIdNumber : 63) + "]");
        }

        // 4.SeqBitLength
        if (options.SeqBitLength < 2 || options.SeqBitLength > 21) {
            throw new MpRuntimeException("SeqBitLength error. (range:[2, 21])");
        }

        // 5.MaxSeqNumber
        int maxSeqNumber = (1 << options.SeqBitLength) - 1;
        if (maxSeqNumber == 0) {
            maxSeqNumber = 63;
        }
        if (options.MaxSeqNumber < 0 || options.MaxSeqNumber > maxSeqNumber) {
            throw new MpRuntimeException("MaxSeqNumber error. (range:[1, " + maxSeqNumber + "]");
        }

        // 6.MinSeqNumber
        if (options.MinSeqNumber < 5 || options.MinSeqNumber > maxSeqNumber) {
            throw new MpRuntimeException("MinSeqNumber error. (range:[5, " + maxSeqNumber + "]");
        }

        // 7.TopOverCostCount
        if (options.TopOverCostCount < 0 || options.TopOverCostCount > 10000) {
            throw new MpRuntimeException("TopOverCostCount error. (range:[0, 10000]");
        }

        switch (options.Method) {
            case 2:
                _SnowWorker = new SnowWorkerM2(options);
                break;
            case 1:
            default:
                _SnowWorker = new SnowWorkerM1(options);
                break;
        }

        if (options.Method == 1) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public long newLong() {
        return _SnowWorker.nextId();
    }
}
