import com.ullink.agent.SampleBean;
import org.junit.Test;

public class AgentTest
{

    @Test
    public void testAgent()
    {
        for (int counter = 0; counter < 100; counter++)
        {
            new SampleBean().sampleMethod();
        }
    }
}
