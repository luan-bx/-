import com.shark.aio.alarm.entity.AlarmEntity;
import com.shark.aio.util.ObjectUtil;
import org.junit.jupiter.api.Test;

public class testUtil {
    @Test
    public void testIsEmpty(){
        AlarmEntity alarmEntity = new AlarmEntity();
        System.out.println(ObjectUtil.isEmpty(alarmEntity));
    }
}
