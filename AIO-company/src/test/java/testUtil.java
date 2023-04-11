import com.shark.aio.alarm.entity.AlarmSettingsEntity;
import com.shark.aio.util.ObjectUtil;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

public class testUtil {
    @Test
    public void testIsEmpty(){
        AlarmSettingsEntity alarmSettingsEntity = new AlarmSettingsEntity();
        System.out.println(ObjectUtil.isEmpty(alarmSettingsEntity));
    }

    @Test
    public void testOS(){
        System.out.println(System.getProperty("os.name"));
    }


}
