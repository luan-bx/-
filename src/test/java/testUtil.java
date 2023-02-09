import com.shark.aio.alarm.entity.AlarmSettingsEntity;
import com.shark.aio.util.ObjectUtil;
import org.junit.jupiter.api.Test;

public class testUtil {
    @Test
    public void testIsEmpty(){
        AlarmSettingsEntity alarmSettingsEntity = new AlarmSettingsEntity();
        System.out.println(ObjectUtil.isEmpty(alarmSettingsEntity));
    }


}
