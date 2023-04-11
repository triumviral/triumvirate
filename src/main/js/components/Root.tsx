import * as React from 'react';
import { GreeterServiceClient } from '../pb/HelloworldServiceClientPb';
import { HelloRequest } from '../pb/helloworld_pb';

export default function Root(props: { url: string }) {
    const [greeter, setGreeter] = React.useState(null);
    React.useEffect(() => {
        const greeterService = new GreeterServiceClient(props.url);
        setGreeter(greeterService);
    }, [props.url]);
    const handleGreeting = React.useCallback(() => {
        var request = new HelloRequest();
        request.setName('World!');
        greeter.sayHello(request, {}, function (err, response) {
            console.log(response.getMessage());
        });
    }, [greeter]);
    return (
        <button onClick={handleGreeting}>
            Say Hello
        </button>
    );
}
