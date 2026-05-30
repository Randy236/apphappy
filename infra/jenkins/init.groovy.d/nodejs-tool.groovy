import jenkins.model.*
import hudson.tools.*
import jenkins.plugins.nodejs.tools.*

def jenkins = Jenkins.getInstance()
def desc = jenkins.getDescriptor(NodeJSInstallation.DescriptorImpl.class)
if (desc == null) {
    return
}

def home = '/usr/bin'
def installations = desc.getInstallations()
def existing = installations.find { it?.name == 'NodeJS 22' }
if (existing != null) {
    existing.setHome(home)
} else {
    def inst = new NodeJSInstallation(
        'NodeJS 22',
        home,
        Collections.emptyList() as ToolProperty[]
    )
    installations = (installations + inst) as NodeJSInstallation[]
}
desc.setInstallations(installations)
jenkins.save()
